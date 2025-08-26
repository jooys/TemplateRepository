plugins {
    id("kotlin")
    kotlin("plugin.serialization")
}

dependencies {
    implementation(projects.shared)

    implementation(libs.kotlin.stdlib)

    implementation(libs.javax.inject)

    implementation(libs.kotlin.serialize)
    implementation(libs.coroutine.android)
    implementation(libs.kotlin.serialization.jvm)

}
