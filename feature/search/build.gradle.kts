plugins {
    id(libs.plugins.custom.feature.library.get().pluginId)
    id(libs.plugins.custom.android.hilt.get().pluginId)
    id(libs.plugins.custom.android.libraryCompose.get().pluginId)
}

android {
    namespace = "com.jooys.template.feature.detail"
}

dependencies {
    implementation(projects.domain)
    implementation(projects.model)
    implementation(projects.core)

    implementation(projects.feature.search.navigation)
    implementation(projects.feature.detail.navigation)
    implementation(projects.feature.bookmark.navigation)

    implementation(libs.logger)
    implementation(libs.bundles.android.core)
    implementation(libs.bundles.compose)
    implementation(libs.compose.preview)
    debugImplementation(libs.compose.preview.tool)
    implementation(libs.coil.compose)
    implementation(libs.coil.network)

    testImplementation(libs.mockk)
    testImplementation(libs.bundles.kotest)
    testImplementation(libs.turbine)
}
