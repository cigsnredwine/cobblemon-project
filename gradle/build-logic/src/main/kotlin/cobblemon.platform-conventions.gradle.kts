import utilities.VersionType
import utilities.writeVersion
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.fabricmc.loom.api.LoomGradleExtensionAPI
import net.fabricmc.loom.configuration.ide.RunConfigSettings
import net.fabricmc.loom.task.RemapJarTask
import org.gradle.api.tasks.bundling.Jar

plugins {
    id("cobblemon.base-conventions")
    id("com.github.johnrengelman.shadow")
}

writeVersion(type = VersionType.FULL)

val bundle: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

extensions.configure<LoomGradleExtensionAPI> {
    val clientConfig: RunConfigSettings = runConfigs.getByName("client")
    clientConfig.runDir("runClient")
    clientConfig.programArg("--username=CobblemonDev")
    // This is AshKetchum's UUID so you get an Ash Ketchum skin.
    clientConfig.programArg("--uuid=93e4e551-589a-41cb-ab2d-435266c8e035")

    val serverConfig: RunConfigSettings = runConfigs.getByName("server")
    serverConfig.runDir("runServer")
}

tasks {
    named<Jar>("jar") {
        archiveBaseName.set("Cobblemon-${project.name}")
        archiveClassifier.set("dev-slim")
    }

    named<ShadowJar>("shadowJar") {
        archiveClassifier.set("dev-shadow")
        archiveBaseName.set("Cobblemon-${project.name}")
        configurations = listOf(bundle)
        mergeServiceFiles()
    }

    named<RemapJarTask>("remapJar") {
        dependsOn("shadowJar")
        inputFile.set(named<ShadowJar>("shadowJar").flatMap { it.archiveFile })
        archiveBaseName.set("Cobblemon-${project.name}")
        archiveVersion.set("${rootProject.version}")
    }

    val copyJar by registering(CopyFile::class) {
        val productionJar = named<RemapJarTask>("remapJar").flatMap { it.archiveFile }
        fileToCopy.set(productionJar)
        destination.set(productionJar.flatMap {
            rootProject.layout.buildDirectory.file("libs/${it.asFile.name}")
        })
    }

    named("assemble") {
        dependsOn(copyJar)
    }

}
