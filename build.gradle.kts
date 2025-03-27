// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    val kotlinVersion = "1.9.20"
    val agpVersion = "8.1.0"
    val composeVersion = "1.6.0"
    
    kotlin("jvm") apply false
    kotlin("multiplatform") apply false
    kotlin("android").version(kotlinVersion).apply(false)
    kotlin("plugin.serialization") apply false
    id("com.android.application").version(agpVersion).apply(false)
    id("com.android.library").version(agpVersion).apply(false)
    id("org.jetbrains.compose").version(composeVersion).apply(false)
    id("org.jetbrains.kotlin.plugin.parcelize").version(kotlinVersion).apply(false)
    id("app.cash.sqldelight").version("2.0.1").apply(false)
}

ext {
    set("kotlin_version", "1.9.24")
}

buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev")
        maven("https://maven.pkg.jetbrains.space/public/p/decompose/dev")
        ivy {
            url = uri("https://download.jetbrains.com/kotlin/native/builds")
            patternLayout {
                artifact("releases/[revision]/[module]-[revision].[ext]")
            }
            metadataSources {
                artifact()
            }
        }
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.20")
        classpath("com.android.tools.build:gradle:8.1.0")
        classpath("org.jetbrains.compose:compose-gradle-plugin:1.6.0")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev")
        maven("https://maven.pkg.jetbrains.space/public/p/decompose/dev")
        ivy {
            url = uri("https://download.jetbrains.com/kotlin/native/builds")
            patternLayout {
                artifact("releases/[revision]/[module]-[revision].[ext]")
            }
            metadataSources {
                artifact()
            }
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}