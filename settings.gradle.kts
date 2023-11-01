rootProject.name = "Noah"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven(url = "https://repo.caramel.moe/repository/maven-public/") // caramel-repo
        maven(url = "https://papermc.io/repo/repository/maven-public/") // papermc-repo
        maven(url = "https://jitpack.io/") // Jitpack(PacketEvents)
        maven(url = "https://oss.sonatype.org/content/groups/public/") // sonatype
        maven(url = "https://repo.dmulloy2.net/repository/public/") // dmulloy2 repo
    }
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS) // only use these repos
}
