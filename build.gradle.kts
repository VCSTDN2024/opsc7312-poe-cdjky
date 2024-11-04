// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}

buildscript {
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.0") // Make sure this is the required version
        classpath("com.android.tools.build:gradle:8.5.1")           // Check your Android Gradle plugin version
        classpath("com.google.gms:google-services:4.4.2")           // Firebase version
    }
}



