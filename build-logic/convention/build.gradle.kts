import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "com.jooys.template.build-logic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

dependencies {
    compileOnly(libs.android.gradle)
    compileOnly(libs.kotlin.gradle)
}

gradlePlugin {
    plugins {
        register("customApplicationPlugins") {
            id = libs.plugins.custom.android.application.get().pluginId
            implementationClass = "com.jooys.template.CustomApplicationPlugin"
        }
        register("customAndroidHiltPlugin") {
            id = libs.plugins.custom.android.hilt.get().pluginId
            implementationClass = "com.jooys.template.CustomAndroidHiltPlugin"
        }
        register("customProjectConfigPlugins") {
            id = libs.plugins.custom.android.applicationConfig.get().pluginId
            implementationClass = "com.jooys.template.CustomApplicationConfigPlugin"
        }
        register("customAndroidLibraryPlugins") {
            id = libs.plugins.custom.android.library.get().pluginId
            implementationClass = "com.jooys.template.CustomAndroidLibraryPlugin"
        }
        register("customJvmLibraryPlugins") {
            id = libs.plugins.custom.jvm.library.get().pluginId
            implementationClass = "com.jooys.template.CustomJvmLibraryPlugin"
        }
        register("customNavigationPlugins") {
            id = libs.plugins.custom.navigation.library.get().pluginId
            implementationClass = "com.jooys.template.CustomAndroidNavigationLibraryPlugin"
        }
        register("customAndroidFeaturePlugins") {
            id = libs.plugins.custom.feature.library.get().pluginId
            implementationClass = "com.jooys.template.CustomAndroidFeatureLibraryPlugin"
        }
        register("customAndroidLibraryComposePlugins") {
            id = libs.plugins.custom.android.libraryCompose.get().pluginId
            implementationClass = "com.jooys.template.CustomAndroidLibraryComposePlugin"
        }
    }
}
