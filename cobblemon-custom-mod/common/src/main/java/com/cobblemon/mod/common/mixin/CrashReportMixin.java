/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.mixin;

import com.cobblemon.mod.common.CobblemonBuildDetails;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CrashReport.class)
public final class CrashReportMixin {

    @Inject(method = "addStackTrace", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/SystemDetails;writeTo(Ljava/lang/StringBuilder;)V"))
    public void cobblemon$printCobblemonDetails(StringBuilder builder, CallbackInfo callback) {
        CrashReportSection cobblemon = new CrashReportSection("Cobblemon");
        cobblemon.add("Version", CobblemonBuildDetails.VERSION);
        cobblemon.add("Is Snapshot", CobblemonBuildDetails.SNAPSHOT);
        cobblemon.add("Git Commit", CobblemonBuildDetails.INSTANCE.smallCommitHash() + " (" + "https://gitlab.com/cable-mc/cobblemon/-/commit/" + CobblemonBuildDetails.GIT_COMMIT + ")");
        cobblemon.add("Branch", CobblemonBuildDetails.BRANCH);

        cobblemon.addStackTrace(builder);
        builder.append("\n\n");
    }

}
