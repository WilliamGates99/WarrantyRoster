pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
//        maven { url = uri("https://artifacts.applovin.com/android") } // Applovin Ad Review Repo
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "WarrantyRoster"
include(":app")