plugins {
    id(libs.plugins.custom.android.application.get().pluginId)
    id(libs.plugins.custom.android.applicationConfig.get().pluginId)
    id(libs.plugins.custom.android.hilt.get().pluginId)
    kotlin("android")
    id("kotlin-parcelize")
}

android {
    namespace = "com.jooys.template"
    compileSdk = 36

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    //Modules
    implementation(projects.domain)
    implementation(projects.data)
    implementation(projects.remote)
    implementation(projects.model)
    implementation(projects.coreDesign)

    implementation(libs.coroutine.android)

    implementation(libs.bundles.android.lifecycle)
    implementation(libs.bundles.android.core)

    implementation(libs.bundles.compose)
    implementation(libs.compose.preview)
    debugImplementation(libs.compose.preview.tool)
    implementation(libs.coil.compose)

    implementation(libs.retrofit.core)

    implementation(libs.logger)
}
