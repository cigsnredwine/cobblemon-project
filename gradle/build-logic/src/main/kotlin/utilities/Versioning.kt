package utilities

import org.gradle.api.Project
import java.io.ByteArrayOutputStream

fun Project.version(): String {
    return rootProject.property("mod_version").toString()
}

fun Project.isSnapshot(): Boolean {
    return rootProject.property("snapshot") == "true"
}

fun Project.writeVersion(type: VersionType = VersionType.FULL): String {
    val version = "${rootProject.property("mod_version")}+${rootProject.property("mc_version")}"
    return when (type) {
        VersionType.PUBLISHING -> if(this.isSnapshot()) "$version-SNAPSHOT" else version
        VersionType.FULL -> if(this.isSnapshot()) rootProject.version.toString() else version
    }
}

fun Project.gitCommitHash(): String {
    return runGitCommand("rev-parse", "HEAD") ?: "Not Specified"
}

fun Project.gitBranchName(): String {
    return runGitCommand("rev-parse", "--abbrev-ref", "HEAD") ?: "local"
}

private fun Project.runGitCommand(vararg args: String): String? {
    val stdout = ByteArrayOutputStream()
    return try {
        rootProject.exec {
            commandLine("git", *args)
            standardOutput = stdout
            isIgnoreExitValue = true
        }
        stdout.toString().trim().ifBlank { null }
    } catch (_: Exception) {
        null
    }
}

enum class VersionType {
    PUBLISHING,
    FULL
}
