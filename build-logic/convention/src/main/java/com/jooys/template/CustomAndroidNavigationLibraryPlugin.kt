package com.jooys.template

import com.jooys.template.extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.apply

class CustomAndroidNavigationLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "custom.android.library")

            dependencies {
                implementation(libs.findLibrary("androidx.appcompat").get())
            }
        }
    }
}
