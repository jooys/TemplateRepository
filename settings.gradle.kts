rootProject.name = "templateProject"

pluginManagement {
    includeBuild("build-logic")

    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

include(":app")
include(":data")
include(":domain")
include(":remote")
include(":model")
include(":local")
include(":core")
include(":feature")
include(":feature:search")
include(":feature:search:navigation")
include(":feature:detail")
include(":feature:detail:navigation")
include(":feature:bookmark")
include(":feature:bookmark:navigation")
