plugins {
    id(libs.plugins.custom.android.library.get().pluginId)
    id(libs.plugins.custom.android.hilt.get().pluginId)
}

android {
    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
    }
    namespace = "com.jooys.template.local"
}

dependencies {
    implementation(projects.data)
    implementation(projects.model)

    implementation(libs.androidx.core)
    implementation(libs.androidx.appcompat)


    //room
    implementation(libs.room.core)
    ksp(libs.room.compiler)
}
