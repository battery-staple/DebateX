buildscript {
    repositories {
        gradlePluginPortal()
        jcenter()
        google()
        maven {
            url = uri("https://dl.bintray.com/kotlin/kotlin-eap")
        }
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.31")
        classpath("com.android.tools.build:gradle:4.0.2")
    }
}

group = "com.rohengiralt"
version = "0.1.0"

plugins {
    id("io.gitlab.arturbosch.detekt") version "1.14.2"
}

detekt {
    reports {
        html.enabled = true
    }
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://dl.bintray.com/kotlin/kotlin-eap")
    }
}
