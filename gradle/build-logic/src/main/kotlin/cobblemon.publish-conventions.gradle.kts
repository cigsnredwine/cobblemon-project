import utilities.VersionType
import utilities.writeVersion
import net.fabricmc.loom.api.LoomGradleExtensionAPI
import net.fabricmc.loom.task.RemapJarTask
import net.fabricmc.loom.task.RemapSourcesJarTask
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication

plugins {
    id("java")
    id("java-library")
    id("maven-publish")
    id("dev.architectury.loom")
}

extensions.configure<JavaPluginExtension> {
    withSourcesJar()
    withJavadocJar()
}

extensions.configure<PublishingExtension> {
    repositories {
        maven("https://maven.impactdev.net/repository/development/") {
            name = "ImpactDev-Public"
            credentials {
                username = System.getenv("COBBLEMON_MAVEN_USER")
                password = System.getenv("COBBLEMON_MAVEN_PASSWORD")
            }
        }
    }

    publications {
        create<MavenPublication>(project.name) {
            artifact(tasks.named<RemapJarTask>("remapJar"))
            artifact(tasks.named<RemapSourcesJarTask>("remapSourcesJar"))

            @Suppress("UnstableApiUsage")
            extensions.getByType(LoomGradleExtensionAPI::class.java).disableDeprecatedPomGeneration(this)

            groupId = "com.cobblemon"
            artifactId = project.findProperty("maven.artifactId")?.toString() ?: project.name
            version = project.writeVersion(VersionType.PUBLISHING)
        }
    }
}
