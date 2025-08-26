package com.jooys.template

import com.android.build.gradle.LibraryExtension
import com.jooys.template.extensions.configureKotlinAndroid
import com.jooys.template.extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

class CustomAndroidLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.android.library")
            apply(plugin = "org.jetbrains.kotlin.android")
            configureAndroidLibrary()
        }
    }

    private fun Project.configureAndroidLibrary() {
        extensions.configure<LibraryExtension> {
            configureKotlinAndroid(this)
            defaultConfig.targetSdk = libs.findVersion("targetSdkVersion").get().toString().toInt()
        }
    }
}
