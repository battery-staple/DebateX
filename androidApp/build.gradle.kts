@file:Suppress("SpellCheckingInspection")

plugins {
    id("com.android.application")
    kotlin("android")
//    id("kotlin-android-extensions")
}
group = "com.rohengiralt"
version = "0.1.0"

repositories {
    gradlePluginPortal()
    google()
    jcenter()
    mavenCentral()
    maven {
        url = uri("https://dl.bintray.com/kotlin/kotlin-eap")
    }
}
dependencies {
    implementation(project(":shared"))
    implementation("androidx.core:core-ktx:1.3.1")
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("androidx.constraintlayout:constraintlayout:1.1.3")
}

android {
    compileSdkVersion(29)
    defaultConfig {
        applicationId = "com.rohengiralt.debatex"
        minSdkVersion(24)
        targetSdkVersion(29)
        versionCode = 1
        versionName = "0.1.0"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}