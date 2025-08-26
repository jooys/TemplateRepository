rootProject.name = "Template"

pluginManagement {
    includeBuild("build-logic")

    repositories {
        google()
        mavenCentral()
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

include(
    ":app",
    ":data",
    ":domain",
    ":remote",
    ":shared",
    ":core-design"
)
