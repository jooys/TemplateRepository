plugins {
    id(libs.plugins.custom.feature.library.get().pluginId)
    id(libs.plugins.custom.android.hilt.get().pluginId)
    id(libs.plugins.custom.android.libraryCompose.get().pluginId)
}

android {
    namespace = "com.jooys.template.feature.bookmark"
}

dependencies {
    implementation(projects.domain)
    implementation(projects.model)
    implementation(projects.core)

    implementation(projects.feature.bookmark.navigation)
    implementation(projects.feature.detail.navigation)

    implementation(libs.bundles.android.core)
    implementation(libs.bundles.compose)
    implementation(libs.compose.preview)
    debugImplementation(libs.compose.preview.tool)
    implementation(libs.coil.compose)

    testImplementation(libs.mockk)
    testImplementation(libs.bundles.kotest)
    testImplementation(libs.turbine)

}
