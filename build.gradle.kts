// Top-level build file where you can add configuration options common to all sub-projects/modules.
//buildscript {
//    dependencies {
//        classpath("com.applovin.quality:AppLovinQualityServiceGradlePlugin:4.3.7")
//    }
//}

plugins {
    id("com.android.application") version "7.3.1" apply false // Android Gradle Plugin Version
    id("com.android.library") version "7.3.1" apply false // Android Gradle Plugin Version
    id("org.jetbrains.kotlin.android") version "1.7.10" apply false
    id("androidx.navigation.safeargs") version "2.5.3" apply false
    id("com.google.gms.google-services") version "4.3.14" apply false // Google Services plugin
    id("com.google.dagger.hilt.android") version "2.44.2" apply false
    id("com.google.firebase.crashlytics") version "2.9.1" apply false
    id("com.google.firebase.firebase-perf") version "1.4.2" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}