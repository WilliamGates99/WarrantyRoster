// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        maven { url = uri("https://artifacts.applovin.com/android") } // Applovin Ad Review Repo
    }
    dependencies {
        classpath("com.applovin.quality:AppLovinQualityServiceGradlePlugin:4.9.3")
    }
}

plugins {
    id("com.android.application") version "8.0.0" apply false // Android Gradle Plugin Version
    id("com.android.library") version "8.0.0" apply false // Android Gradle Plugin Version
    id("org.jetbrains.kotlin.android") version "1.8.20" apply false
    id("androidx.navigation.safeargs") version "2.5.3" apply false
    id("com.google.gms.google-services") version "4.3.14" apply false // Google Services plugin
    id("com.google.dagger.hilt.android") version "2.45" apply false
    id("com.google.firebase.crashlytics") version "2.9.5" apply false
    id("com.google.firebase.firebase-perf") version "1.4.2" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}