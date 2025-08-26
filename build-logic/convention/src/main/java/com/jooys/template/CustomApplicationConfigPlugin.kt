package com.jooys.template

import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.jooys.template.extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

@Suppress("SpellCheckingInspection")
class CustomApplicationConfigPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.extensions.configure<BaseAppModuleExtension> {
            defaultConfig {
                applicationId = "com.jooys.template"
                versionCode = target.libs.findVersion("versionCode").get().toString().toInt()
                versionName = target.libs.findVersion("versionName").get().toString()
            }
        }
    }
}
