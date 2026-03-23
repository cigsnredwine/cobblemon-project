import utilities.gitBranchName

plugins {
    id("cobblemon.root-conventions")
}

version = "${project.property("mod_version")}+${project.property("mc_version")}"

val isSnapshot = project.property("snapshot")?.equals("true") ?: false
if (isSnapshot) {
    val fixedBranchName = gitBranchName().substringAfter("/")
    val buildNumber = System.getProperty("buildNumber") ?: "local"
    version = "$version-${fixedBranchName}-${buildNumber}"
}
