/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.block.multiblock

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.api.events.pokemon.FossilRevivedEvent
import com.cobblemon.mod.common.api.fossil.Fossil
import com.cobblemon.mod.common.api.fossil.Fossils
import com.cobblemon.mod.common.api.fossil.NaturalMaterials
import com.cobblemon.mod.common.api.multiblock.MultiblockEntity
import com.cobblemon.mod.common.api.multiblock.MultiblockStructure
import com.cobblemon.mod.common.api.tags.CobblemonItemTags
import com.cobblemon.mod.common.block.entity.FossilMultiblockEntity
import com.cobblemon.mod.common.block.FossilAnalyzerBlock
import com.cobblemon.mod.common.block.MonitorBlock
import com.cobblemon.mod.common.block.RestorationTankBlock
import com.cobblemon.mod.common.block.entity.RestorationTankBlockEntity
import com.cobblemon.mod.common.client.render.models.blockbench.fossil.FossilState
import com.cobblemon.mod.common.client.sound.CancellableSoundController
import com.cobblemon.mod.common.client.sound.CancellableSoundInstance
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.item.PokeBallItem
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.*
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.HorizontalFacingBlock
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.entity.EntityPose
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtHelper
import net.minecraft.nbt.NbtList
import net.minecraft.registry.Registries
import net.minecraft.registry.tag.FluidTags
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.ItemScatterer
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.minecraft.world.World
import java.util.UUID
import kotlin.math.ceil

class FossilMultiblockStructure (
    val monitorPos: BlockPos,
    val analyzerPos: BlockPos,
    val tankBasePos: BlockPos,
        animAge: Int = -1,
        animPartialTicks: Float = 0F
) : MultiblockStructure {

    override val controllerBlockPos = analyzerPos


    // TODO: API method for this
    var organicMaterialInside = 0
        private set

    var hasCreatedPokemon: Boolean = false
        private set
    var timeRemaining = -1
        private set
    var resultingFossil: Fossil? = null
        private set
    private var lastInteraction: Long = 0
    private var machineStartTime: Long = 0
    private var protectionTime: Int = -1
    private var fossilOwnerUUID: UUID? = null
    val fossilState = FossilState(animAge, animPartialTicks)
    var fossilInventory: MutableList<ItemStack> = mutableListOf<ItemStack>()
    var tankConnectorDirection: Direction? = null

    //Only updated clientside
    var fillLevel = 0
    override fun onUse(
        blockState: BlockState,
        world: World,
        blockPos: BlockPos,
        player: PlayerEntity,
        interactionHand: Hand,
        blockHitResult: BlockHitResult
    ): ActionResult {
        val stack = player.getStackInHand(interactionHand)

        if(stack.isIn(CobblemonItemTags.POKE_BALLS) || stack.item is PokeBallItem) {
            if (player !is ServerPlayerEntity) {
                return ActionResult.SUCCESS
            }
            if (this.hasCreatedPokemon) {
                if (this.fossilOwnerUUID != null && player.uuid != this.fossilOwnerUUID) {
                    var ownerName : String = "UNKNOWN_USER" // TODO: lang agnostic fallback
                    server()?.userCache?.getByUuid(this.fossilOwnerUUID)?.orElse(null)?.name?.let {
                        ownerName = it
                    }
                    player.sendMessage(lang("fossilmachine.protected", ownerName), true)
                    return ActionResult.FAIL
                }


                val ballType = (stack.item as PokeBallItem).pokeBall
                if (!player.isCreative) {
                    stack?.decrement(1)
                }

                val pokemon = this.resultingFossil?.result?.create()

                if(pokemon != null) {
                    pokemon.caughtBall = ballType
                    player.party().add(pokemon)
                    this.fossilState.growthState = "Taken"
                    player.playSound(CobblemonSounds.FOSSIL_MACHINE_RETRIEVE_POKEMON, SoundCategory.BLOCKS, 1.0F, 1.0F)
                    CobblemonEvents.FOSSIL_REVIVED.post(FossilRevivedEvent(pokemon, player))
                }

                // Turn the monitor off
                val monitorState = world.getBlockState(monitorPos)
                if(monitorState.contains(MonitorBlock.SCREEN) && !monitorState.equals(MonitorBlock.MonitorScreen.OFF)) {
                    world.setBlockState(monitorPos, monitorState.with(MonitorBlock.SCREEN, MonitorBlock.MonitorScreen.OFF))
                }

                this.hasCreatedPokemon = false
                this.fossilOwnerUUID = null
                this.protectionTime = -1
                this.updateFossilType(world)
                this.syncToClient(world)
                this.markDirty(world)
                return ActionResult.SUCCESS
            }
        }

        // Reclaim the last fossil from the machine if their hand is empty
        if (player.getStackInHand(interactionHand).isEmpty) {
            if(!this.isRunning() && !this.hasCreatedPokemon) {
                if (fossilInventory.isEmpty()) {
                    return ActionResult.CONSUME
                }
                if(player is ServerPlayerEntity) {
                    player.setStackInHand(interactionHand, fossilInventory.last())
                    // remove last fossil in the fossil machine stack when grabbed out of the machine
                    this.fossilInventory.removeAt(fossilInventory.size - 1)
                    world.playSound(null, analyzerPos, CobblemonSounds.FOSSIL_MACHINE_RETRIEVE_FOSSIL, SoundCategory.BLOCKS)
                    this.updateFossilType(world)
                    this.syncToClient(world)
                    this.markDirty(world)
                }
            }
            return ActionResult.CONSUME
        }

        // Check if the player is holding a fossil and if so insert it into the machine.
        if (Fossils.isFossilIngredient(stack)) {
            if (!this.isRunning() && !this.hasCreatedPokemon) {
                if (fossilInventory.size > Cobblemon.config.maxInsertedFossilItems) {
                    return ActionResult.FAIL
                }
                if (player is ServerPlayerEntity) {
                    val copyFossilStack = stack.copyWithCount(1)
                    if (!player.isCreative) {
                        stack?.decrement(1)
                    }
                    fossilOwnerUUID = player.uuid
                    fossilInventory.add(copyFossilStack)
                    this.updateFossilType(world)
                    world.playSound(null, analyzerPos, CobblemonSounds.FOSSIL_MACHINE_INSERT_FOSSIL, SoundCategory.BLOCKS)
                    this.syncToClient(world)
                    this.markDirty(world)
                }
            }
            return ActionResult.SUCCESS
        }

        // Check if the player is holding a natural material and if so, feed it to the machine.
        if (NaturalMaterials.isNaturalMaterial(stack)) {
            if (player is ServerPlayerEntity
                    && !this.isRunning()
                    && !this.hasCreatedPokemon
                    && this.organicMaterialInside < MATERIAL_TO_START
                    && insertOrganicMaterial(ItemStack(stack.item, 1), world)) {
                this.lastInteraction = world.time
                if (!player.isCreative) {
                    val returnItem = NaturalMaterials.getReturnItem(stack)
                    stack?.decrement(1)
                    player.giveOrDropItemStack(ItemStack(Registries.ITEM.get(returnItem)), false)
                }
            }
            return ActionResult.success(world.isClient)
        }

        // pure client instances dont know what a valid fossil is so this is my janky workaround
        if (stack.isIn(CobblemonItemTags.FOSSILS)) return ActionResult.SUCCESS

        return ActionResult.PASS
    }

    public fun spawn(world: World, pos: BlockPos, directionToBehind: Direction, pokemon: Pokemon) : Boolean {
        val entity = PokemonEntity(world, pokemon = pokemon)
        entity.calculateDimensions()
        val width = entity.boundingBox.xLength

        val idealPlace = pos.add(directionToBehind.vector.multiply(ceil(width / 2.0).toInt() + 1))
        var box = entity.getDimensions(EntityPose.STANDING).getBoxAt(idealPlace.toCenterPos().subtract(0.0, 0.5, 0.0))

        for (i in 0..5) {
            box = box.offset(directionToBehind.vector.x.toDouble(), 0.0, directionToBehind.vector.z.toDouble())
            val fixedPosition = makeSuitableY(world, idealPlace.add(directionToBehind.vector), entity, box)
            if (fixedPosition != null) {
                entity.setPosition(fixedPosition.toCenterPos().subtract(0.0, 0.5, 0.0))
                // TODO: Find a correct way to set the new entity's Yaw rotation. (Face away from the machine)
                if (world.spawnEntity(entity)) {
                    CobblemonEvents.FOSSIL_REVIVED.post(FossilRevivedEvent(pokemon, null))
                    return true
                } else {
                    Cobblemon.LOGGER.warn("Couldn't spawn resurrected Pokémon for some reason")
                }
                break
            }
        }
        return false
    }

    fun isSafeFloor(world: World, pos: BlockPos, entity: PokemonEntity): Boolean {
        val state = world.getBlockState(pos)
        return if (state.isAir) {
            false
        } else if (state.hasSolidTopSurface(world, pos, entity) || state.isSolidSurface(world, pos, entity, Direction.DOWN)) {
            true
        } else if ((entity.behaviour.moving.swim.canWalkOnWater || entity.behaviour.moving.swim.canSwimInWater) && state.fluidState.isIn(FluidTags.WATER)) {
            true
        } else {
            (entity.behaviour.moving.swim.canWalkOnLava || entity.behaviour.moving.swim.canSwimInLava) && state.fluidState.isIn(FluidTags.LAVA)
        }
    }

    fun makeSuitableY(world: World, pos: BlockPos, entity: PokemonEntity, box: Box): BlockPos? {
        if (world.canCollide(entity, box)) {
            for (i in 1..15) {
                val newBox = box.offset(0.5, i.toDouble(), 0.5)

                if (!world.canCollide(entity, newBox) && isSafeFloor(world, pos.add(0, i - 1, 0), entity)) {
                    return pos.add(0, i, 0)
                }
            }
        } else {
            for (i in 1..15) {
                val newBox = box.offset(0.5, -i.toDouble(), 0.5)

                if (world.canCollide(entity, newBox) && isSafeFloor(world, pos.add(0, -i, 0), entity)) {
                    return pos.add(0, -i + 1, 0)
                }
            }
        }

        return null
    }

    @Deprecated("Deprecated in Java")
    override fun getComparatorOutput(state: BlockState, world: World?, pos: BlockPos?): Int {
        if(world == null || pos == null) {
            return 0
        }
        if(monitorPos == pos) {
            if(hasCreatedPokemon) {
                return 15
            }
            if(!isRunning()) {
                return 0
            }
            return Math.max(15 - timeRemaining * 15 / TIME_TO_TAKE, 1)
        }
        if(tankBasePos == pos || tankBasePos.up() == pos) {
            return organicMaterialInside * 15 / MATERIAL_TO_START
        }
        return 0
    }
    override fun onTriggerEvent(state: BlockState?, world: ServerWorld?, pos: BlockPos?, random: Random?) {
        // instantiate the pokemon as a new entity and spawn it at the location of the machine
        if(this.protectionTime <= 0) {
            val wildPokemon: Pokemon = if (hasCreatedPokemon) resultingFossil?.result?.create() ?: return else return
            val direction = state?.get(HorizontalFacingBlock.FACING)?.opposite
            if(pos != null && direction != null && world != null) {
                val success = this.spawn(world, pos, direction, wildPokemon)
                if(success) {
                    this.fossilState.growthState = "Taken"
                    this.hasCreatedPokemon = false
                    this.fossilOwnerUUID = null
                    this.protectionTime = -1
                    world.playSound(null, tankBasePos, CobblemonSounds.FOSSIL_MACHINE_RETRIEVE_POKEMON, SoundCategory.BLOCKS)
                    this.updateFossilType(world)
                    this.syncToClient(world)
                    this.markDirty(world)
                }
            }
        }
    }

    override fun onBreak(world: World, pos: BlockPos, state: BlockState, player: PlayerEntity?) {
        val monitorEntity = world.getBlockEntity(monitorPos) as? MultiblockEntity
        val analyzerEntity = world.getBlockEntity(analyzerPos) as? MultiblockEntity
        val tankBaseEntity = world.getBlockEntity(tankBasePos) as? MultiblockEntity
        val tankTopEntity = world.getBlockEntity(tankBasePos.up()) as? MultiblockEntity
        val tankBaseBlockState = world.getBlockState(tankBaseEntity?.pos)
        val direction = tankBaseBlockState.get(HorizontalFacingBlock.FACING).opposite
        val wildPokemon: Pokemon? = if(hasCreatedPokemon) resultingFossil?.result?.create() else null

        monitorEntity?.multiblockStructure = null
        analyzerEntity?.multiblockStructure = null
        tankBaseEntity?.multiblockStructure = null
        tankTopEntity?.multiblockStructure = null
        monitorEntity?.masterBlockPos = null
        analyzerEntity?.masterBlockPos = null
        tankBaseEntity?.masterBlockPos = null
        tankTopEntity?.masterBlockPos = null

        // Drop fossils from machine as long as the machine is not started or near completion
        if (this.timeRemaining == -1 || this.timeRemaining >= 20) {
            this.fossilInventory.forEach {
                val stack = ItemStack(it.item, 1)
                ItemScatterer.spawn(world, pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), stack)
            }
        }
        if(tankBaseEntity is RestorationTankBlockEntity) {
            tankBaseEntity.inv.clearToList().forEach {
                ItemScatterer.spawn(world, pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), it)
            }
        }


        // if the machine is broken while the pokemon is done then spawn the pokemon at the location and make it a wild pokemon
        if (wildPokemon != null) {
            //world.createExplosion(this.createdPokemon?.entity, pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), 5F, World.ExplosionSourceType.TNT)

            // instantiate the pokemon as a new entity and spawn it at the location of the machine
            //var wildPokemon = this.createdPokemon?.sendOut(world as ServerWorld, pos.toVec3d())

            //world.spawnEntity(wildPokemon)
            this.spawn(world, pos, direction, wildPokemon)

        }
        this.protectionTime = -1
        this.updateFossilType(world)
        this.stopMachine(world)
        this.syncToClient(world)
        this.markDirty(world)
    }

    override fun markRemoved(world: World) {
        if(world.isClient) {
            CancellableSoundController.stopSound(this.tankBasePos, CobblemonSounds.FOSSIL_MACHINE_ACTIVE_LOOP.id)
        }
    }

    override fun tick(world: World) {
        if (protectionTime > 0) protectionTime--
        if (protectionTime == 0) {
            protectionTime = -1
            this.fossilOwnerUUID = null
            this.updateProgress(world)
            this.syncToClient(world)
            this.markDirty(world)
            world.playSound(null, tankBasePos, CobblemonSounds.FOSSIL_MACHINE_UNPROTECTED, SoundCategory.BLOCKS)
        }

        if (this.hasCreatedPokemon) {
            return
        }

        if (world.isClient && this.isRunning() && (world.time - this.machineStartTime) % 160L == 0L) {
            if(world.isClient) {
                CancellableSoundController.playSound(CancellableSoundInstance(CobblemonSounds.FOSSIL_MACHINE_ACTIVE_LOOP,
                        tankBasePos, true, 1.0f, 1.0f, ))
            }
        }

        if (this.timeRemaining == -1 && this.organicMaterialInside >= MATERIAL_TO_START && this.resultingFossil != null) {
            this.startMachine(world)
            return
        }

        if (this.timeRemaining >= 0) {
            this.timeRemaining--
        }

        if (this.timeRemaining % TIME_PER_STAGE == 0) {
            this.updateProgress(world)
            this.syncToClient(world)
            this.markDirty(world)
        }

        if (this.timeRemaining == 0) {
            world.playSound(null, tankBasePos, CobblemonSounds.FOSSIL_MACHINE_FINISHED, SoundCategory.BLOCKS)
            fossilInventory.clear()
            this.hasCreatedPokemon = true
            if(this.fossilOwnerUUID != null) {
                protectionTime = PROTECTION_TIME
            }

            this.stopMachine(world)
        }
    }

    override fun syncToClient(world: World) {
        val tankBaseEntity = world.getBlockEntity(tankBasePos) as? MultiblockEntity
        val analyzerEntity = world.getBlockEntity(controllerBlockPos) as? MultiblockEntity
        val monitorEntity = world.getBlockEntity(monitorPos) as? MultiblockEntity

        if(tankBaseEntity != null)
            world.updateListeners(tankBasePos, tankBaseEntity.cachedState, tankBaseEntity.cachedState, Block.NOTIFY_LISTENERS)
        if(analyzerEntity != null)
            world.updateListeners(analyzerPos, analyzerEntity.cachedState, analyzerEntity.cachedState, Block.NOTIFY_LISTENERS)
        if(monitorEntity != null)
            world.updateListeners(monitorPos, monitorEntity.cachedState, monitorEntity.cachedState, Block.NOTIFY_LISTENERS)
    }

    override fun markDirty(world: World) {
        val entities = listOf(
            world.getBlockEntity(analyzerPos),
            world.getBlockEntity(tankBasePos),
            world.getBlockEntity(tankBasePos.up()),
            world.getBlockEntity(monitorPos)
        )
        entities.forEach {
            it?.markDirty()
        }
    }

    fun startMachine(world: World) {
        this.timeRemaining = TIME_TO_TAKE
        this.machineStartTime = world.time

        world.playSound(null, tankBasePos, CobblemonSounds.FOSSIL_MACHINE_ACTIVATE, SoundCategory.BLOCKS)
        if(world.isClient) {
            CancellableSoundController.playSound(CancellableSoundInstance(CobblemonSounds.FOSSIL_MACHINE_ACTIVE_LOOP,
                    tankBasePos, true, 1.0f, 1.0f, ))
        }

        this.updateOnStatus(world)
        this.updateProgress(world)
        this.syncToClient(world)
        this.markDirty(world)
    }

    fun stopMachine(world: World){
        this.fossilState.growthState = "Fully Grown"
        this.timeRemaining = -1
        this.organicMaterialInside = 0

        fossilInventory.clear()

        if(world.isClient) {
            CancellableSoundController.stopSound(tankBasePos, CobblemonSounds.FOSSIL_MACHINE_ACTIVE_LOOP.id)
        }

        this.updateOnStatus(world)
        this.updateProgress(world)
        this.syncToClient(world)
        this.markDirty(world)
    }

    fun updateOnStatus(world: World) {
        val upperTankPos = tankBasePos.up()
        val analyzerState = world.getBlockState(analyzerPos)
        val tankState = world.getBlockState(tankBasePos.up())
        if (analyzerState.contains(FossilAnalyzerBlock.ON)) {
            world.setBlockState(analyzerPos, analyzerState.with(FossilAnalyzerBlock.ON, timeRemaining >= 0))
        }
        if (tankState.contains(RestorationTankBlock.ON)) {
            world.setBlockState(upperTankPos, tankState.with(RestorationTankBlock.ON, timeRemaining >= 0))
        }
    }

    fun updateProgress(world: World) {
        val monitorState = world.getBlockState(monitorPos)
        if (monitorState.contains(MonitorBlock.SCREEN)) {
            val screenID = if (protectionTime > 0F) {
                MonitorBlock.MonitorScreen.GREEN_PROGRESS_9
            } else if (timeRemaining <= 0) {
                MonitorBlock.MonitorScreen.OFF
            } else {
                getProgressScreen((TIME_TO_TAKE - timeRemaining) / TIME_PER_STAGE)
            }
            world.setBlockState(monitorPos, monitorState.with(MonitorBlock.SCREEN, screenID))
        }
    }

    fun getProgressScreen(progress:Int) : MonitorBlock.MonitorScreen {
        return when (progress) {
            0 -> MonitorBlock.MonitorScreen.BLUE_PROGRESS_1
            1 -> MonitorBlock.MonitorScreen.BLUE_PROGRESS_2
            2 -> MonitorBlock.MonitorScreen.BLUE_PROGRESS_3
            3 -> MonitorBlock.MonitorScreen.BLUE_PROGRESS_4
            4 -> MonitorBlock.MonitorScreen.BLUE_PROGRESS_5
            5 -> MonitorBlock.MonitorScreen.BLUE_PROGRESS_6
            6 -> MonitorBlock.MonitorScreen.BLUE_PROGRESS_7
            7 -> MonitorBlock.MonitorScreen.BLUE_PROGRESS_8
            8 -> MonitorBlock.MonitorScreen.BLUE_PROGRESS_9
            else -> MonitorBlock.MonitorScreen.OFF
        }
    }

    /**
     * Checks for a resulting fossil type inside the machine.
     * @param world The world to check in.
     * @return The resulting fossil type if found, otherwise null.
     */
    fun updateFossilType(world: World) {
        if (fossilInventory.isEmpty()) {
            if (this.resultingFossil == null) {
                return
            }
            this.resultingFossil = null
        } else {
            this.resultingFossil = Fossils.getFossilByItemStacks(fossilInventory)
        }
    }

    /**
     * Checks if the machine is currently running.
     */
    fun isRunning(): Boolean {
        return this.timeRemaining > 0
    }

    //Returns false if material wasnt inserted
    fun insertOrganicMaterial(stack: ItemStack, world: World): Boolean {
        var natureValue = NaturalMaterials.getContent(stack)
        if (timeRemaining > 0 || this.organicMaterialInside >= MATERIAL_TO_START || natureValue == null) {
            return false
        }
        natureValue *= stack.count

        if (natureValue <= 0 && organicMaterialInside == 0) return false
        val oldFillStage = organicMaterialInside * 8 / MATERIAL_TO_START

        // to prevent over/under filling the tank causing a crash
        if ((organicMaterialInside + natureValue) > MATERIAL_TO_START) {
            organicMaterialInside = MATERIAL_TO_START
        } else if ((organicMaterialInside + natureValue) < 0) {
            organicMaterialInside = 0
        } else {
            organicMaterialInside += natureValue
        }
        if (this.organicMaterialInside >= MATERIAL_TO_START) {
            world.playSound(null, this.tankBasePos, CobblemonSounds.FOSSIL_MACHINE_DNA_FULL, SoundCategory.BLOCKS, 1.0F, 1.0F)
        } else if (world.time - this.lastInteraction < 10) {
            world.playSound(null, this.tankBasePos, CobblemonSounds.FOSSIL_MACHINE_INSERT_DNA_SMALL, SoundCategory.BLOCKS, 1.0F, 1.0F)
        } else {
            world.playSound(null, this.tankBasePos, CobblemonSounds.FOSSIL_MACHINE_INSERT_DNA, SoundCategory.BLOCKS, 1.0F, 1.0F)
        }
        this.markDirty(world)
        if (oldFillStage != (organicMaterialInside * 8 / MATERIAL_TO_START)) {
            this.syncToClient(world)
        }
        return true
    }

    // insert fossil to fossilInventory - returns false if failed
    fun insertFossil(stack: ItemStack, world: World): Boolean {
        // if machine is running or fossil inventory is equal to 3 return false
        if (timeRemaining > 0 || this.fossilInventory.size == 3) {
            return false
        }
        val oldFillStage = this.fossilInventory.size

        //add fossil to the stack in the Compartment
        this.fossilInventory.add(stack)
        world.playSound(null, analyzerPos, CobblemonSounds.FOSSIL_MACHINE_INSERT_FOSSIL, SoundCategory.BLOCKS)

        this.updateFossilType(world)
        this.markDirty(world)
        if (oldFillStage != this.fossilInventory.size) {
            this.syncToClient(world)
        }
        return true
    }

    override fun writeToNbt(): NbtCompound {
        val result = NbtCompound()
        result.put(DataKeys.MONITOR_POS, NbtHelper.fromBlockPos(monitorPos))
        result.put(DataKeys.ANALYZER_POS, NbtHelper.fromBlockPos(analyzerPos))
        result.put(DataKeys.TANK_BASE_POS, NbtHelper.fromBlockPos(tankBasePos))
        result.putInt(DataKeys.TIME_LEFT, timeRemaining)
        result.putInt(DataKeys.PROTECTED_TIME_LEFT, protectionTime)
        if(fossilOwnerUUID != null)
            result.putUuid(DataKeys.FOSSIL_OWNER, fossilOwnerUUID)
        result.putInt(DataKeys.ORGANIC_MATERIAL, organicMaterialInside)
        val fossilInv = NbtList()
        fossilInventory.forEach{ item ->
            fossilInv.add(item.writeNbt(NbtCompound()))
        }
        result.put(DataKeys.FOSSIL_INVENTORY, fossilInv)
        result.putString(DataKeys.CONNECTOR_DIRECTION, tankConnectorDirection?.toString())

        if (this.resultingFossil != null) {
            result.putString(DataKeys.INSERTED_FOSSIL, this.resultingFossil!!.asString())
        }

        result.putBoolean(DataKeys.HAS_CREATED_POKEMON, hasCreatedPokemon)

        return result
    }

    companion object {
        val TICKER = BlockEntityTicker<FossilMultiblockEntity> { world, _, _, blockEntity ->
            if (blockEntity.multiblockStructure != null) {
                blockEntity.multiblockStructure!!.tick(world)
            }
        }

        const val TICKS_PER_MINUTE = 1200
        const val MATERIAL_TO_START = 128
        const val TIME_TO_TAKE = TICKS_PER_MINUTE * 12
        const val TIME_PER_STAGE = TIME_TO_TAKE / 8
        const val PROTECTION_TIME = TICKS_PER_MINUTE * 5

        fun fromNbt(nbt: NbtCompound, animAge: Int = -1, partialTicks: Float = 0f): FossilMultiblockStructure {
            val monitorPos = NbtHelper.toBlockPos(nbt.getCompound(DataKeys.MONITOR_POS))
            val compartmentPos = NbtHelper.toBlockPos(nbt.getCompound(DataKeys.ANALYZER_POS))
            val tankBasePos = NbtHelper.toBlockPos(nbt.getCompound(DataKeys.TANK_BASE_POS))

            val result = FossilMultiblockStructure(monitorPos, compartmentPos, tankBasePos, animAge, partialTicks)
            result.organicMaterialInside = nbt.getInt(DataKeys.ORGANIC_MATERIAL)
            result.timeRemaining = nbt.getInt(DataKeys.TIME_LEFT)
            result.protectionTime = if(nbt.contains(DataKeys.PROTECTED_TIME_LEFT)) nbt.getInt(DataKeys.PROTECTED_TIME_LEFT) else -1
            result.fossilOwnerUUID = if(nbt.contains(DataKeys.FOSSIL_OWNER)) nbt.getUuid(DataKeys.FOSSIL_OWNER) else null

            val fossilInv = (nbt.get(DataKeys.FOSSIL_INVENTORY) as NbtList)
            val actualFossilList = mutableListOf<ItemStack>()
            fossilInv.forEach {
                actualFossilList.add(ItemStack.fromNbt(it as NbtCompound))
            }
            result.fossilInventory = actualFossilList
            result.tankConnectorDirection = Direction.byName(nbt.getString(DataKeys.CONNECTOR_DIRECTION))

            if (nbt.contains(DataKeys.INSERTED_FOSSIL)) {
                val id = Identifier(nbt.getString(DataKeys.INSERTED_FOSSIL))
                val fossil = Fossils.getByIdentifier(id)

                if (fossil != null) {
                    result.resultingFossil = fossil
                } else {
                    Cobblemon.LOGGER.error("Loaded fossil structure with invalid fossil type: {}", id)
                }
            }

            if (nbt.contains(DataKeys.CREATED_POKEMON)) {
                // migration of instances that saved the created pokeon in the nbt
                result.hasCreatedPokemon = true
            } else if (nbt.contains(DataKeys.HAS_CREATED_POKEMON)){
                result.hasCreatedPokemon = nbt.getBoolean(DataKeys.HAS_CREATED_POKEMON)
            }
            result.fillLevel = result.organicMaterialInside * 8 / MATERIAL_TO_START
            return result
        }

    }
}