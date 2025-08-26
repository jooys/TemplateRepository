package com.jooys.template

import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.project

fun DependencyHandler.implementationProject(path: String) {
    add("implementation", project(path))
}

fun DependencyHandler.implementation(dependencyName: Any) {
    add("implementation", dependencyName)
}

fun DependencyHandler.testImplementation(dependencyName: Any) {
    add("testImplementation", dependencyName)
}

fun DependencyHandler.debugImplementation(dependencyName: Any) {
    add("debugImplementation", dependencyName)
}

fun DependencyHandler.kapt(dependencyName: Any) {
    add("kapt", dependencyName)
}

fun DependencyHandler.compileOnly(dependencyName: Any) {
    add("compileOnly", dependencyName)
}

fun DependencyHandler.ksp(dependencyName: Any) {
    add("ksp", dependencyName)
}
