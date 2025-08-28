package com.jooys.template

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.apply

class CustomAndroidFeatureLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "custom.android.library")

            dependencies {
                implementation(project(":domain"))
                implementation(project(":model"))
                implementation(project(":core-design"))
            }
        }
    }
}
