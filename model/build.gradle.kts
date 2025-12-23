plugins {
    id(libs.plugins.custom.jvm.library.get().pluginId)
    id("kotlin")
    kotlin("plugin.serialization")
}

dependencies {
    implementation(libs.kotlin.serialize)
    implementation(libs.kotlin.serialization.jvm)
}
