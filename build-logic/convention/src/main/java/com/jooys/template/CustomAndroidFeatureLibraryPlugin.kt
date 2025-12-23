package com.jooys.template

import com.android.build.gradle.TestedExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.findByType

class CustomAndroidFeatureLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "custom.android.library")

            extensions.findByType<TestedExtension>()?.run {
                testOptions.unitTests.all {
                    it.useJUnitPlatform()
                }
            }
            dependencies {
                implementation(project(":domain"))
                implementation(project(":model"))
                implementation(project(":core"))
            }
        }
    }
}
