@file:Suppress("UnstableApiUsage")

import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs.kotlin")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services") // Google Services plugin
    id("com.google.firebase.crashlytics")
    id("com.google.firebase.firebase-perf")
    id("applovin-quality-service")
}

val properties = gradleLocalProperties(rootDir)

applovin {
    apiKey =
        "wPNW70TAHp3xrcTl2HOeZEpvt5kn19fKosui1hEugVWrFDKCh_411hghugSX5ln5ewRrMMRB8W1Ce_S3hcPh4c"
}

android {
    namespace = "com.xeniac.warrantyroster_manager"
    compileSdk = 34
    buildToolsVersion = "34.0.0"

    defaultConfig {
        applicationId = "com.xeniac.warrantyroster_manager"
        minSdk = 21
        targetSdk = 34
        versionCode = 22 // TODO UPGRADE AFTER EACH RELEASE
        versionName = "2.1.2" // TODO UPGRADE AFTER EACH RELEASE

        /**
         * Keeps language resources for only the locales specified below.
         */
        resourceConfigurations.addAll(listOf("en-rUS", "en-rGB", "fa-rIR"))

        resValue(
            "string",
            "fb_login_protocol_scheme",
            properties.getProperty("FACEBOOK_AUTH_LOGIN_PROTOCOL_SCHEME")
        )

        resValue(
            "string",
            "facebook_app_id",
            properties.getProperty("FACEBOOK_AUTH_APP_ID")
        )

        resValue(
            "string",
            "facebook_client_token",
            properties.getProperty("FACEBOOK_AUTH_CLIENT_TOKEN")
        )

        buildConfigField(
            "String",
            "GOOGLE_AUTH_SERVER_CLIENT_ID",
            properties.getProperty("GOOGLE_AUTH_SERVER_CLIENT_ID")
        )

        buildConfigField(
            "String",
            "CATEGORY_MISCELLANEOUS_ICON",
            properties.getProperty("CATEGORY_MISCELLANEOUS_ICON")
        )

        testInstrumentationRunner = "com.xeniac.warrantyroster_manager.HiltTestRunner"
    }

    buildTypes {
        getByName("debug") {
            versionNameSuffix = " - debug"
            applicationIdSuffix = ".debug"

            resValue("color", "appIconBackground", "#FF9100") // Orange
        }

        getByName("release") {
            /**
             * Enables code shrinking, obfuscation, and optimization for only
             * your project's release build type.
             */
            isMinifyEnabled = true

            /**
             * Enables resource shrinking, which is performed by the Android Gradle plugin.
             */
            isShrinkResources = true

            /**
             * Includes the default ProGuard rules files that are packaged with the Android Gradle plugin.
             */
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    flavorDimensions += listOf("build", "market")
    productFlavors {
        create("dev") {
            dimension = "build"
            versionNameSuffix = " - Developer Preview"
            applicationIdSuffix = ".dev"
            resValue("color", "appIconBackground", "#FF0011") // Red
        }

        create("prod") {
            dimension = "build"
            resValue("color", "appIconBackground", "#1C53F4") // Blue
        }

        create("playStore") {
            dimension = "market"
            buildConfigField(
                "String",
                "URL_APP_STORE",
                "\"https://play.google.com/store/apps/details?id=com.xeniac.warrantyroster_manager\""
            )
            buildConfigField(
                "String",
                "PACKAGE_NAME_APP_STORE",
                "\"com.android.vending\""
            )
        }

        create("amazon") {
            dimension = "market"
            buildConfigField(
                "String",
                "URL_APP_STORE",
                "\"https://www.amazon.com/gp/product/B09PSK6W9Z\""
            )
            buildConfigField(
                "String",
                "PACKAGE_NAME_APP_STORE",
                "\"com.amazon.venezia\""
            )
        }

        create("cafeBazaar") {
            dimension = "market"
            buildConfigField(
                "String",
                "URL_APP_STORE",
                "\"https://cafebazaar.ir/app/com.xeniac.warrantyroster_manager\""
            )
            buildConfigField(
                "String",
                "PACKAGE_NAME_APP_STORE",
                "\"com.farsitel.bazaar\""
            )
        }
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
        dataBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    bundle {
        language {
            /**
             * Specifies that the app bundle should not support configuration APKs for language resources.
             * These resources are instead packaged with each base and dynamic feature APK.
             */
            enableSplit = false
        }
    }
}

androidComponents {
    beforeVariants { variantBuilder ->
        /**
         * Gradle ignores any variants that satisfy the conditions below.
         */
        if (variantBuilder.buildType == "debug") {
            variantBuilder.productFlavors.let {
                variantBuilder.enable = when {
                    it.containsAll(listOf("build" to "dev", "market" to "amazon")) -> false
                    it.containsAll(listOf("build" to "dev", "market" to "cafeBazaar")) -> false
                    it.containsAll(listOf("build" to "prod", "market" to "amazon")) -> false
                    it.containsAll(listOf("build" to "prod", "market" to "cafeBazaar")) -> false
                    it.containsAll(listOf("build" to "prod", "market" to "playStore")) -> false
                    else -> true
                }
            }
        }

        if (variantBuilder.buildType == "release") {
            variantBuilder.productFlavors.let {
                variantBuilder.enable = when {
                    it.containsAll(listOf("build" to "dev", "market" to "amazon")) -> false
                    it.containsAll(listOf("build" to "dev", "market" to "cafeBazaar")) -> false
                    else -> true
                }
            }
        }
    }
}

kapt {
    /**
     * Allow references to generated code
     */
    correctErrorTypes = true
}

dependencies {
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.core:core-splashscreen:1.0.1")

    // Navigation Component
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.1")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.1")

    // Dagger - Hilt
    implementation("com.google.dagger:hilt-android:2.47")
    kapt("com.google.dagger:hilt-compiler:2.47")

    // Activity KTX for Injecting ViewModels into Fragments
    implementation("androidx.activity:activity-ktx:1.7.2")

    // Architectural Components
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Coroutines Support for Firebase
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    // Preferences DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // Firebase BoM and Analytics
    implementation(platform("com.google.firebase:firebase-bom:32.2.3"))
    implementation("com.google.firebase:firebase-analytics-ktx")

    // Firebase App Check
    implementation("com.google.firebase:firebase-appcheck-playintegrity")
    implementation("com.google.firebase:firebase-appcheck-debug")

    // Firebase Release & Monitor
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-perf-ktx")

    // Firebase Auth
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.android.gms:play-services-auth:20.6.0")
    implementation("com.facebook.android:facebook-login:16.2.0")

    // Firebase Firestore, Storage
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")

    // Timber Library
    implementation("com.jakewharton.timber:timber:5.0.1")

    // Lottie Library
    implementation("com.airbnb.android:lottie:6.1.0")

    // Coil Library
    implementation("io.coil-kt:coil:2.4.0")
    implementation("io.coil-kt:coil-svg:2.4.0")

    // Dots Indicator Library
    implementation("com.tbuonomo:dotsindicator:5.0")

    // Google Play In-App Reviews API
    implementation("com.google.android.play:review-ktx:2.0.1")

    // AppLovin Libraries
    implementation("com.applovin:applovin-sdk:11.11.3")
    implementation("com.google.android.gms:play-services-ads-identifier:18.0.1")
    implementation("com.applovin.mediation:google-adapter:22.3.0.0")

    // Google AdMob Library
    implementation("com.google.android.gms:play-services-ads:22.3.0")

    // Tapsell Library
    implementation("ir.tapsell.plus:tapsell-plus-sdk-android:2.2.0")

    // Local Unit Test Libraries
    testImplementation("com.google.truth:truth:1.1.5")
    testImplementation("junit:junit:4.13.2")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")

    // Instrumentation Test Libraries
    androidTestImplementation("com.google.truth:truth:1.1.5")
    androidTestImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test:core:1.4.0") // DO NOT UPGRADE
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.arch.core:core-testing:2.2.0")
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.47")
    kaptAndroidTest("com.google.dagger:hilt-compiler:2.47")

    // UI Test Libraries
    androidTestImplementation("androidx.navigation:navigation-testing:2.7.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0") // DO NOT UPGRADE
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.3.0") // DO NOT UPGRADE
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.3.0") // DO NOT UPGRADE
    debugImplementation("androidx.fragment:fragment-testing:1.6.1")
}

val releaseRootDir = "${rootDir}/app"
val destDir: String = properties.getProperty("DESTINATION_DIR")
val obfuscationDestDir: String = properties.getProperty("OBFUSCATION_DESTINATION_DIR")

val versionName = "${android.defaultConfig.versionName}"
val renamedFileName = "Warranty Roster $versionName"

tasks.register<Copy>("copyDevPreviewApk") {
    val apkFile = "app-dev-playStore-release.apk"
    val apkSourceDir = "${releaseRootDir}/devPlayStore/release/${apkFile}"

    from(apkSourceDir)
    into(destDir)
    rename(apkFile, "$renamedFileName (Developer Preview).apk")
}

tasks.register<Copy>("copyReleaseApk") {
    val amazonApkFile = "app-prod-amazon-release.apk"
    val cafeBazaarApkFile = "app-prod-cafeBazaar-release.apk"

    val amazonApkSourceDir = "${releaseRootDir}/prodAmazon/release/${amazonApkFile}"
    val cafeBazaarApkSourceDir = "${releaseRootDir}/prodCafeBazaar/release/${cafeBazaarApkFile}"

    from(amazonApkSourceDir)
    into(destDir)

    from(cafeBazaarApkSourceDir)
    into(destDir)

    rename(amazonApkFile, "$renamedFileName - Amazon.apk")
    rename(cafeBazaarApkFile, "$renamedFileName - CafeBazaar.apk")
}

tasks.register<Copy>("copyReleaseBundle") {
    val playStoreBundleFile = "app-prod-playStore-release.aab"
    val playStoreBundleSourceDir = "${releaseRootDir}/prodPlayStore/release/${playStoreBundleFile}"

    from(playStoreBundleSourceDir)
    into(destDir)
    rename(playStoreBundleFile, "${renamedFileName}.aab")
}

tasks.register<Copy>("copyObfuscationFolder") {
    val obfuscationSourceDir = "${rootDir}/app/obfuscation"

    from(obfuscationSourceDir)
    into(obfuscationDestDir)
}

tasks.register("copyReleaseFiles") {
    dependsOn("copyReleaseApk", "copyReleaseBundle", "copyObfuscationFolder")
}