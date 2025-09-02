@file:Suppress("UnstableApiUsage")

import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.google.services) // Google Services plugin
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.firebase.perf)
}

val properties = gradleLocalProperties(
    projectRootDir = rootDir,
    providers = providers
)

android {
    namespace = "com.xeniac.warrantyroster_manager"
    compileSdk = 36
    buildToolsVersion = "36.0.0"

    defaultConfig {
        applicationId = "com.xeniac.warrantyroster_manager"
        minSdk = 23
        targetSdk = 36
        versionCode = 24
        versionName = "2.2.0-Alpha1"

        testInstrumentationRunner = "com.xeniac.warrantyroster_manager.HiltTestRunner"

        buildConfigField(
            type = "String",
            name = "CATEGORY_MISCELLANEOUS_ICON",
            value = properties.getProperty("CATEGORY_MISCELLANEOUS_ICON")
        )

        buildConfigField(
            type = "String",
            name = "AUTH_GOOGLE_SERVER_CLIENT_ID",
            value = properties.getProperty("AUTH_GOOGLE_SERVER_CLIENT_ID")
        )

        resValue(
            type = "string",
            name = "fb_login_protocol_scheme",
            value = properties.getProperty("AUTH_FACEBOOK_LOGIN_PROTOCOL_SCHEME")
        )

        resValue(
            type = "string",
            name = "facebook_app_id",
            value = properties.getProperty("AUTH_FACEBOOK_APP_ID")
        )

        resValue(
            type = "string",
            name = "facebook_client_token",
            value = properties.getProperty("AUTH_FACEBOOK_CLIENT_TOKEN")
        )
    }

    androidResources {
        generateLocaleConfig = true

        // Keeps language resources for only the locales specified below.
        localeFilters.addAll(listOf("en-rUS", "en-rGB", "fa-rIR"))
    }

    signingConfigs {
        create("release") {
            storeFile = file(path = properties.getProperty("KEY_STORE_PATH"))
            storePassword = properties.getProperty("KEY_STORE_PASSWORD")
            keyAlias = properties.getProperty("KEY_ALIAS")
            keyPassword = properties.getProperty("KEY_PASSWORD")
        }
    }

    buildTypes {
        debug {
            versionNameSuffix = " - Debug"
            applicationIdSuffix = ".dev.debug"

            resValue(
                type = "color",
                name = "appIconBackground",
                value = "#FFFF9100" // Orange
            )
        }

        create("dev") {
            versionNameSuffix = " - Developer Preview"
            applicationIdSuffix = ".dev"

            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
            ndk.debugSymbolLevel = "FULL" // Include native debug symbols file in app bundle

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            resValue(
                type = "color",
                name = "appIconBackground",
                value = "#FFFF0011" // Red
            )
        }

        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
            ndk.debugSymbolLevel = "FULL" // Include native debug symbols file in app bundle

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            resValue(
                type = "color",
                name = "appIconBackground",
                value = "#FF1C53F4" // Blue
            )
        }
    }

    flavorDimensions += listOf("market")
    productFlavors {
        create("playStore") {
            dimension = "market"
            isDefault = true

            buildConfigField(
                type = "String",
                name = "URL_APP_STORE",
                value = "\"https://play.google.com/store/apps/details?id=com.xeniac.warrantyroster_manager\""
            )

            buildConfigField(
                type = "String",
                name = "PACKAGE_NAME_APP_STORE",
                value = "\"com.android.vending\""
            )
        }

        create("gitHub") {
            dimension = "market"

            buildConfigField(
                type = "String",
                name = "URL_APP_STORE",
                value = "\"https://github.com/WilliamGates99/WarrantyRoster\""
            )

            buildConfigField(
                type = "String",
                name = "PACKAGE_NAME_APP_STORE",
                value = "\"\""
            )
        }

        create("cafeBazaar") {
            dimension = "market"

            buildConfigField(
                type = "String",
                name = "URL_APP_STORE",
                value = "\"https://cafebazaar.ir/app/com.xeniac.warrantyroster_manager\""
            )

            buildConfigField(
                type = "String",
                name = "PACKAGE_NAME_APP_STORE",
                value = "\"com.farsitel.bazaar\""
            )
        }

        create("myket") {
            dimension = "market"

            buildConfigField(
                type = "String",
                name = "URL_APP_STORE",
                value = "\"https://myket.ir/app/com.xeniac.warrantyroster_manager\""
            )

            buildConfigField(
                type = "String",
                name = "PACKAGE_NAME_APP_STORE",
                value = "\"ir.mservices.market\""
            )
        }
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    compileOptions {
        // Java 8+ API Desugaring Support
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.fromTarget(target = "17")

            // Enable Context-Sensitive Resolution in Kotlin 2.2
            freeCompilerArgs.add("-Xcontext-sensitive-resolution")
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    bundle {
        language {
            /*
            Specifies that the app bundle should not support configuration APKs for language resources.
            These resources are instead packaged with each base and dynamic feature APK.
             */
            enableSplit = false
        }
    }
}

hilt {
    enableAggregatingTask = true
}

androidComponents {
    beforeVariants { variantBuilder ->
        variantBuilder.apply {
            // Gradle ignores any variants that satisfy the conditions below.
            if (buildType == "debug") {
                productFlavors.let {
                    enable = when {
                        it.containsAll(listOf("market" to "gitHub")) -> false
                        it.containsAll(listOf("market" to "cafeBazaar")) -> false
                        it.containsAll(listOf("market" to "myket")) -> false
                        else -> true
                    }
                }
            }

            if (buildType == "dev") {
                productFlavors.let {
                    enable = when {
                        it.containsAll(listOf("market" to "gitHub")) -> false
                        it.containsAll(listOf("market" to "cafeBazaar")) -> false
                        it.containsAll(listOf("market" to "myket")) -> false
                        else -> true
                    }
                }
            }

            if (buildType == "release") {
                enable = true
            }
        }
    }
}

dependencies {
    // Java 8+ API Desugaring Support
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    implementation(libs.bundles.essentials)

    // Jetpack Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.bundles.compose)

    // Architectural Components
    implementation(libs.bundles.architectural.components)

    // Dagger - Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    // Coroutines
    implementation(libs.bundles.coroutines)

    // Ktor Client Library
    implementation(libs.bundles.ktor)

    // Preferences DataStore
    implementation(libs.datastore.preferences)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)

    // Credential Manager Library for Login with Google
    implementation(libs.bundles.credential.manager)

    // In-App Browser
    implementation(libs.browser)

    // Timber Library
    implementation(libs.timber)

    // Lottie Library
    implementation(libs.lottie.compose)

    // Coil Library
    implementation(platform(libs.coil.bom))
    implementation(libs.bundles.coil)

    // Google Play In-App APIs
    implementation(libs.bundles.google.play.inapp.apis)

    // Local Unit Test Libraries
    testImplementation(libs.bundles.local.unit.tests)

    // Instrumentation Test Libraries
    androidTestImplementation(libs.bundles.instrumentation.tests)
    kspAndroidTest(libs.hilt.android.compiler)

    // UI Test Libraries
    androidTestImplementation(libs.bundles.ui.tests)
    androidTestImplementation(platform(libs.compose.bom))
    debugImplementation(libs.compose.ui.test.manifest)
}


val releaseRootDir = "${rootDir}/app"
val apkDestDir: String = properties.getProperty("APK_DESTINATION_DIR")
val bundleDestDir: String = properties.getProperty("BUNDLE_DESTINATION_DIR")
val obfuscationDestDir: String = properties.getProperty("OBFUSCATION_DESTINATION_DIR")

val versionName = "${android.defaultConfig.versionName}"
val renamedFileName = "Warranty Roster $versionName"

tasks.register<Copy>(name = "copyDevPreviewBundle") {
    val bundleFile = "app-playStore-dev.aab"
    val bundleSourceDir = "${releaseRootDir}/playStore/dev/${bundleFile}"

    from(bundleSourceDir)
    into(bundleDestDir)

    rename(bundleFile, "$renamedFileName (Developer Preview).aab")
}

tasks.register<Copy>("copyDevPreviewApk") {
    val apkFile = "app-playStore-dev.apk"
    val apkSourceDir = "${releaseRootDir}/playStore/dev/${apkFile}"

    from(apkSourceDir)
    into(apkDestDir)

    rename(apkFile, "$renamedFileName (Developer Preview).aab")
}

tasks.register<Copy>(name = "copyReleaseApk") {
    val gitHubApkFile = "app-gitHub-release.apk"
    val cafeBazaarApkFile = "app-cafeBazaar-release.apk"
    val myketApkFile = "app-myket-release.apk"

    val gitHubApkSourceDir = "${releaseRootDir}/gitHub/release/${gitHubApkFile}"
    val cafeBazaarApkSourceDir = "${releaseRootDir}/cafeBazaar/release/${cafeBazaarApkFile}"
    val myketApkSourceDir = "${releaseRootDir}/myket/release/${myketApkFile}"

    from(gitHubApkSourceDir)
    into(apkDestDir)

    from(cafeBazaarApkSourceDir)
    into(apkDestDir)

    from(myketApkSourceDir)
    into(apkDestDir)

    rename(gitHubApkFile, "$renamedFileName - GitHub.apk")
    rename(cafeBazaarApkFile, "$renamedFileName - CafeBazaar.apk")
    rename(myketApkFile, "$renamedFileName - Myket.apk")
}

tasks.register<Copy>(name = "copyReleaseBundle") {
    val playStoreBundleFile = "app-playStore-release.aab"
    val playStoreBundleSourceDir = "${releaseRootDir}/playStore/release/${playStoreBundleFile}"

    from(playStoreBundleSourceDir)
    into(bundleDestDir)

    rename(playStoreBundleFile, "${renamedFileName}.aab")
}

tasks.register<Copy>(name = "copyObfuscationFolder") {
    val obfuscationSourceDir = "${rootDir}/app/obfuscation"

    from(obfuscationSourceDir)
    into(obfuscationDestDir)
}

tasks.register("copyReleaseFiles") {
    dependsOn("copyReleaseApk", "copyReleaseBundle", "copyObfuscationFolder")
}