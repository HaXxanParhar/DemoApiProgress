pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
      google()
        jcenter()
        maven("https://jitpack.io")
        maven("https://oss.jfrog.org/artifactory/libs-snapshot/")
        maven("https://oss.jfrog.org/artifactory/oss-snapshot-local")
        mavenCentral()
    }
}

rootProject.name = "DemoApiProgress"
include(":app")
 