plugins {
    id(libs.plugins.custom.android.library.get().pluginId)
    id(libs.plugins.custom.android.hilt.get().pluginId)
}

android {
    namespace = "com.jooys.template.remote"
}

dependencies {
    //Modules
    implementation(projects.model)
    implementation(projects.data)

    //Retrofit2 (Network)
    implementation(libs.kotlin.serialize)
    api(libs.bundles.okhttp)
    api(libs.bundles.retrofit)
    api(libs.kotlinXSerializeConverter)

    //DateTime
    implementation(libs.jodaTime)


    implementation(libs.logger)
}
