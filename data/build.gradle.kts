plugins {
    id(libs.plugins.custom.android.library.get().pluginId)
    id(libs.plugins.custom.android.hilt.get().pluginId)
}

android {
    namespace = "com.jooys.template.data"
}

dependencies {
    //Modules
    implementation(projects.model)
    implementation(projects.domain)

    implementation(libs.coroutine.android)
}
