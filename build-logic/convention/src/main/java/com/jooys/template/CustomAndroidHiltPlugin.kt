package com.jooys.template

import com.jooys.template.extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.apply

class CustomAndroidHiltPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.google.devtools.ksp")
            apply(plugin = "dagger.hilt.android.plugin")

            dependencies {
                implementation(libs.findLibrary("hilt.android").get())
                ksp(libs.findLibrary("hilt.compile").get())
            }
        }
    }
}
