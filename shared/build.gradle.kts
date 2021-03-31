@file:Suppress("UNUSED_VARIABLE")

import org.jetbrains.kotlin.gradle.plugin.mpp.Framework.BitcodeEmbeddingMode
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.tasks.FatFrameworkTask

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization") version "1.4.31"
    id("com.android.library")
//    id("kotlin-android-extensions")
}

group = "com.rohengiralt"
version = "0.1.0"

repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
    jcenter()
    maven {
        url = uri("https://dl.bintray.com/kotlin/kotlin-eap")
    }
    maven { url = uri("https://dl.bintray.com/suparnatural/kotlin-multiplatform") }
    maven { url = uri("https://dl.bintray.com/ekito/koin") }
}

val suparnaturalFsVersion: String = "1.0.10"
val settingsVersion: String = "0.6.2"
val serializationVersion: String = "1.1.0"
val atomicfuVersion: String = "0.15.1"
val mockkVersion: String = "1.10.0"
val koinVersion: String = "3.0.1-beta-2"
val coroutinesVersion: String = "1.4.3-native-mt"
val klockVersion: String = "2.0.7"

buildscript {
    dependencies {
        classpath("org.jetbrains.kotlinx:atomicfu-gradle-plugin:0.14.3")
        classpath("io.insert-koin:koin-gradle-plugin:3.0.1-beta-2")
    }
}

apply(plugin = "kotlinx-atomicfu")
apply(plugin = "koin")

kotlin {
    android()

    ios {
        binaries {
            framework {
                baseName = "shared"
                embedBitcode(BitcodeEmbeddingMode.BITCODE)
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("reflect"))
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")
                implementation("com.soywiz.korlibs.klock:klock:$klockVersion")
                implementation("com.russhwolf:multiplatform-settings-no-arg:$settingsVersion")
                implementation("com.benasher44:uuid:0.2.0")
                implementation("org.jetbrains.kotlinx:atomicfu:$atomicfuVersion")
                implementation("io.insert-koin:koin-core:$koinVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion") {
                    version {
                        strictly(coroutinesVersion)
                    }
                }
//                implementation("suparnatural-kotlin-multiplatform:fs-metadata:$suparnaturalFsVersion")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation("io.mockk:mockk-common:$mockkVersion")
                implementation("com.russhwolf:multiplatform-settings-test:$settingsVersion")
                implementation("io.insert-koin:koin-test:$koinVersion")
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("androidx.core:core-ktx:1.3.2")
                implementation("com.soywiz.korlibs.klock:klock-jvm:$klockVersion")
//                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")
//                implementation("suparnatural-kotlin-multiplatform:fs-android:$suparnaturalFsVersion")
            }
        }
        val androidTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("io.mockk:mockk:$mockkVersion")
                implementation("io.insert-koin:koin-test-junit4:$koinVersion")
            }
        }
        val iosMain by getting
        val iosTest by getting
        val iosX64Main by getting {
            dependencies {
//                implementation("suparnatural-kotlin-multiplatform:fs-iosx64:$suparnaturalFsVersion")
            }
        }
        val iosX64Test by getting
        val iosArm64Main by getting {
            dependencies {
//                implementation("suparnatural-kotlin-multiplatform:fs-iosarm64:$suparnaturalFsVersion")
            }
        }
        val iosArm64Test by getting

        all {
            languageSettings.enableLanguageFeature("InlineClasses")
        }
    }

    targets.all {
        compilations.all {
            @Suppress("SuspiciousCollectionReassignment")
            kotlinOptions {
                freeCompilerArgs += "-Xskip-prerelease-check"
                freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
//                languageVersion = "1.5"
//                apiVersion = "1.5"
            }
        }
    }
}

android {
    compileSdkVersion(29)
    defaultConfig {
        minSdkVersion(24)
        targetSdkVersion(29)
        versionCode = 1
        versionName = "1.0"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}

val skinnyFramework: Sync by tasks.creating(Sync::class) {
    group = "build"
//    val mode = System.getenv("CONFIGURATION") ?: "DEBUG"
    val framework = kotlin.targets.getByName<KotlinNativeTarget>("iosArm64").binaries.getFramework(org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType.RELEASE)

//    inputs.property("mode", mode)
    dependsOn(framework.linkTask)
    val targetDir = File(buildDir, "xcode-frameworks")
    from({ framework.outputDirectory })
    into(targetDir)
}

val simulatorSkinnyFramework: Sync by tasks.creating(Sync::class) {
    group = "build"
//    val mode = System.getenv("CONFIGURATION") ?: "DEBUG"
    val framework = kotlin.targets.getByName<KotlinNativeTarget>("iosX64").binaries.getFramework(org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType.RELEASE)
//    inputs.property("mode", mode)
    dependsOn(framework.linkTask)
    val targetDir = File(buildDir, "xcode-frameworks")
    from({ framework.outputDirectory })
    into(targetDir)
}

val fatFramework: FatFrameworkTask by tasks.creating(FatFrameworkTask::class) {
    group = "build"

    val mode = System.getenv("CONFIGURATION") ?: "DEBUG"

    baseName = "shared"

    // The default destination directory is '<build directory>/fat-framework'.
    destinationDir = buildDir.resolve("xcode-frameworks")

    // Specify the frameworks to be merged.
    from(
        listOf("iosArm64", "iosX64").map {
            kotlin.targets.getByName<KotlinNativeTarget>(it).binaries.getFramework(mode)
        }
    )

    doLast {
        val gradlew = File(destinationDir, "gradlew")
        gradlew.writeText(
            """#!/bin/bash
        export 'JAVA_HOME=${System.getProperty("java.home")}'
        cd '${rootProject.rootDir}'
        ./gradlew $@"""
                .trimIndent()
        )
        gradlew.setExecutable(true)
    }
}

tasks.getByName("build").dependsOn(fatFramework)

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.4.2")
}