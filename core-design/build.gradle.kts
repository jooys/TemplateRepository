plugins {
    id(libs.plugins.custom.android.library.get().pluginId)
    id(libs.plugins.custom.android.libraryCompose.get().pluginId)
}

android {
    namespace = "com.jooys.template.core_design"
}

dependencies {
    implementation(libs.androidx.appcompat)

    implementation(libs.javax.inject)

    implementation(libs.bundles.compose)
    implementation(libs.compose.preview)
    implementation(libs.compose.preview.tool)
    implementation(libs.coil.compose)
}
