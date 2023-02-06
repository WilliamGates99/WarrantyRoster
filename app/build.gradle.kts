@file:Suppress("UnstableApiUsage")

import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.gms.google-services") // Google Services plugin
    id("dagger.hilt.android.plugin")
    id("com.google.firebase.crashlytics")
    id("com.google.firebase.firebase-perf")
//    id("applovin-quality-service")
}

val properties = gradleLocalProperties(rootDir)

//applovin {
//    apiKey = properties.getProperty("APPLOVIN_API_KEY")
//}

android {
    namespace = "com.xeniac.warrantyroster_manager"
    compileSdk = 33
    buildToolsVersion = "33.0.1"

    defaultConfig {
        applicationId = "com.xeniac.warrantyroster_manager"
        minSdk = 21
        targetSdk = 33
        versionCode = 19 // TODO UPGRADE AFTER EACH RELEASE
        versionName = "2.0.1" // TODO UPGRADE AFTER EACH RELEASE

        /**
         * Keeps language resources for only the locales specified below.
         */
        resourceConfigurations += mutableSetOf("en-rUS", "en-rGB", "fa-rIR")

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
            "APPLOVIN_INTERSTITIAL_UNIT_ID",
            properties.getProperty("APPLOVIN_INTERSTITIAL_UNIT_ID")
        )
        buildConfigField(
            "String",
            "APPLOVIN_SETTINGS_NATIVE_UNIT_ID",
            properties.getProperty("APPLOVIN_SETTINGS_NATIVE_UNIT_ID")
        )
        buildConfigField(
            "String",
            "APPLOVIN_WARRANTIES_NATIVE_UNIT_ID",
            properties.getProperty("APPLOVIN_WARRANTIES_NATIVE_UNIT_ID")
        )
        buildConfigField(
            "String",
            "TAPSELL_KEY",
            properties.getProperty("TAPSELL_KEY")
        )
        buildConfigField(
            "String",
            "TAPSELL_INTERSTITIAL_ZONE_ID",
            properties.getProperty("TAPSELL_INTERSTITIAL_ZONE_ID")
        )
        buildConfigField(
            "String",
            "TAPSELL_WARRANTIES_NATIVE_ZONE_ID",
            properties.getProperty("TAPSELL_WARRANTIES_NATIVE_ZONE_ID")
        )
        buildConfigField(
            "String",
            "TAPSELL_SETTINGS_NATIVE_ZONE_ID",
            properties.getProperty("TAPSELL_SETTINGS_NATIVE_ZONE_ID")
        )

        testInstrumentationRunner = "com.xeniac.warrantyroster_manager.HiltTestRunner"
    }

    buildTypes {
        getByName("debug") {
            versionNameSuffix = " - debug"
            applicationIdSuffix = ".debug"
        }

        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
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
            resValue("color", "appIconBackground", "@color/appIconBackgroundDev")
        }

        create("prod") {
            dimension = "build"
            resValue("color", "appIconBackground", "@color/appIconBackgroundProd")
        }

        create("playStore") {
            dimension = "market"
            buildConfigField(
                "String",
                "URL_APP_STORE",
                properties.getProperty("URL_PLAY_STORE")
            )
            buildConfigField(
                "String",
                "PACKAGE_NAME_APP_STORE",
                properties.getProperty("PACKAGE_NAME_PLAY_STORE")
            )
        }

        create("amazon") {
            dimension = "market"
            buildConfigField(
                "String",
                "URL_APP_STORE",
                properties.getProperty("URL_AMAZON")
            )
            buildConfigField(
                "String",
                "PACKAGE_NAME_APP_STORE",
                properties.getProperty("PACKAGE_NAME_AMAZON")
            )
        }

        create("cafeBazaar") {
            dimension = "market"
            buildConfigField(
                "String",
                "URL_APP_STORE",
                properties.getProperty("URL_CAFEBAZAAR")
            )
            buildConfigField(
                "String",
                "PACKAGE_NAME_APP_STORE",
                properties.getProperty("PACKAGE_NAME_CAFEBAZAAR")
            )
        }
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
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
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.0")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.core:core-splashscreen:1.0.0")

    // Navigation Component
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.3")

    // Dagger - Hilt
    implementation("com.google.dagger:hilt-android:2.44.2")
    kapt("com.google.dagger:hilt-compiler:2.44.2")

    // Activity KTX for Injecting ViewModels into Fragments
    implementation("androidx.activity:activity-ktx:1.6.1")

    // Architectural Components
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")

    // Coroutines Support for Firebase
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.4")

    // Preferences DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // Firebase BoM and Analytics
    implementation(platform("com.google.firebase:firebase-bom:31.1.1"))
    implementation("com.google.firebase:firebase-analytics-ktx")

    // Firebase App Check
    implementation("com.google.firebase:firebase-appcheck-playintegrity")
    implementation("com.google.firebase:firebase-appcheck-debug")

    // Firebase Release & Monitor
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-perf-ktx")

    // Firebase Auth
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.android.gms:play-services-auth:20.4.1")
    implementation("com.facebook.android:facebook-login:15.2.0")

    // Firebase Firestore, Storage
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")

    // Timber Library
    implementation("com.jakewharton.timber:timber:5.0.1")

    // Lottie Library
    implementation("com.airbnb.android:lottie:5.2.0")

    // Coil Library
    implementation("io.coil-kt:coil:2.2.2")
    implementation("io.coil-kt:coil-svg:2.2.2")

    // Dots Indicator Library
    implementation("com.tbuonomo:dotsindicator:4.3")

    // Google Play In-App Reviews API
    implementation("com.google.android.play:review-ktx:2.0.1")

    // AppLovin Libraries
    implementation("com.applovin:applovin-sdk:11.7.0")
    implementation("com.google.android.gms:play-services-ads-identifier:18.0.1")
    implementation("com.applovin.mediation:google-adapter:21.5.0.0")

    // Google AdMob Library
    implementation("com.google.android.gms:play-services-ads:21.5.0")

    // Tapsell Library
    implementation("ir.tapsell.plus:tapsell-plus-sdk-android:2.1.8")

    // Local Unit Test Libraries
    testImplementation("com.google.truth:truth:1.1.3")
    testImplementation("junit:junit:4.13.2")
    testImplementation("androidx.arch.core:core-testing:2.1.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")

    // Instrumentation Test Libraries
    androidTestImplementation("com.google.truth:truth:1.1.3")
    androidTestImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test:core:1.5.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.arch.core:core-testing:2.1.0")
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
    androidTestImplementation("androidx.navigation:navigation-testing:2.5.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.5.1")
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.44.2")
    kaptAndroidTest("com.google.dagger:hilt-compiler:2.44.2")
    debugImplementation("androidx.fragment:fragment-testing:1.5.5")
}

tasks.register<Copy>("copyDevPreviewApk") {
    val releaseRootDir = "${rootDir}/app"
    val destinationDir = "D:\\01 My Files\\Projects\\Xeniac\\Warranty Roster\\APK"

    val versionName = "${android.defaultConfig.versionName}"
    val renamedFileName = "Warranty Roster $versionName (Developer Preview)"

    val apkFile = "app-dev-playStore-release.apk"
    val apkSourceDir = "${releaseRootDir}/devPlayStore/release/${apkFile}"

    from(apkSourceDir)
    into(destinationDir)

    rename(apkFile, "${renamedFileName}.apk")
}

tasks.register<Copy>("copyReleaseApk") {
    val releaseRootDir = "${rootDir}/app"
    val destinationDir = "D:\\01 My Files\\Projects\\Xeniac\\Warranty Roster\\APK"

    val versionName = "${android.defaultConfig.versionName}"
    val renamedFileName = "Warranty Roster $versionName"

    val amazonApkFile = "app-prod-amazon-release.apk"
    val cafeBazaarApkFile = "app-prod-cafeBazaar-release.apk"

    val amazonApkSourceDir = "${releaseRootDir}/prodAmazon/release/${amazonApkFile}"
    val cafeBazaarApkSourceDir = "${releaseRootDir}/prodCafeBazaar/release/${cafeBazaarApkFile}"

    from(amazonApkSourceDir)
    into(destinationDir)

    from(cafeBazaarApkSourceDir)
    into(destinationDir)

    rename(amazonApkFile, "$renamedFileName - Amazon.apk")
    rename(cafeBazaarApkFile, "$renamedFileName - CafeBazaar.apk")
}

tasks.register<Copy>("copyReleaseBundle") {
    val releaseRootDir = "${rootDir}/app"
    val destinationDir = "D:\\01 My Files\\Projects\\Xeniac\\Warranty Roster\\APK"

    val versionName = "${android.defaultConfig.versionName}"
    val renamedFileName = "Warranty Roster $versionName"

    val playStoreBundleFile = "app-prod-playStore-release.aab"
    val playStoreBundleSourceDir = "${releaseRootDir}/prodPlayStore/release/${playStoreBundleFile}"

    from(playStoreBundleSourceDir)
    into(destinationDir)

    rename(playStoreBundleFile, "${renamedFileName}.aab")
}

tasks.register<Copy>("copyObfuscationFolder") {
    val obfuscationSourceDir = "${rootDir}/app/obfuscation"
    val obfuscationDestDir = "D:\\01 My Files\\Projects\\Xeniac\\Warranty Roster\\APK\\obfuscation"

    from(obfuscationSourceDir)
    into(obfuscationDestDir)
}

tasks.register("copyReleaseFiles") {
    dependsOn("copyReleaseApk", "copyReleaseBundle", "copyObfuscationFolder")
}