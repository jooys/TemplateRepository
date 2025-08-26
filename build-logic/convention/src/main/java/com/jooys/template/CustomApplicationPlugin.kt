package com.jooys.template

import com.android.build.api.dsl.ApplicationExtension
import com.jooys.template.extensions.configureAndroidCompose
import com.jooys.template.extensions.configureKotlinAndroid
import com.jooys.template.extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType

@Suppress("SpellCheckingInspection")
class CustomApplicationPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.android.application")
            apply(plugin = "org.jetbrains.kotlin.android")

            apply(plugin = "org.jetbrains.kotlin.plugin.compose")

            configureAndroid()
            val extension = extensions.getByType<ApplicationExtension>()
            configureAndroidCompose(extension)
        }
    }


    private fun Project.configureAndroid() {
        extensions.configure<ApplicationExtension> {
            this@configureAndroid.configureKotlinAndroid(this)
            defaultConfig.targetSdk = libs.findVersion("targetSdkVersion").get().toString().toInt()
        }
    }
}

