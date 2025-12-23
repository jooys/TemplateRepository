plugins {
    id(libs.plugins.custom.jvm.library.get().pluginId)
    id("kotlin")
    kotlin("plugin.serialization")
}

dependencies {
    implementation(projects.model)

    implementation(libs.kotlin.stdlib)

    implementation(libs.javax.inject)

    implementation(libs.kotlin.serialize)
    implementation(libs.coroutine.core)
    implementation(libs.kotlin.serialization.jvm)
}
