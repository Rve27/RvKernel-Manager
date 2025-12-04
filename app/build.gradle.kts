/*
 * Copyright (c) 2025 Rve <rve27github@gmail.com>
 * All Rights Reserved.
 */
import java.util.Properties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.rve.rvkernelmanager"
    compileSdk = 36
    buildToolsVersion = "36.1.0"
    ndkVersion = "29.0.14206865"

    signingConfigs {
        create("release") {
            val propertiesFile = rootProject.file("signing.properties")
            val properties = if (propertiesFile.exists()) {
                Properties().apply {
                    load(propertiesFile.inputStream())
                }
            } else {
                null
            }

            val getString: (String, String, String) -> String? = { propertyName, environmentName, prompt ->
                properties?.getProperty(propertyName) ?: System.getenv(environmentName)
                    ?: System.console()?.readLine("\n$prompt: ")
            }

            storeFile = getString("storeFile", "STORE_FILE", "Store file")?.let { rootProject.file(it) }
            storePassword = getString("storePassword", "STORE_PASSWORD", "Store password")
            keyAlias = getString("keyAlias", "KEY_ALIAS", "Key alias")
            keyPassword = getString("keyPassword", "KEY_PASSWORD", "Key password")
        }
    }

    defaultConfig {
        applicationId = "com.rve.rvkernelmanager"
        minSdk = 31
        targetSdk = 36
        versionCode = 129
        versionName = "1.2.9"

        vectorDrawables {
            useSupportLibrary = true
        }

        ndk {
            //noinspection ChromeOsAbiSupport
            abiFilters += listOf("arm64-v8a", "armeabi-v7a")
        }
    }

    dependenciesInfo {
        // Disables dependency metadata when building APKs (for IzzyOnDroid/F-Droid)
        includeInApk = false
        // Disables dependency metadata when building Android App Bundles (for Google Play)
        includeInBundle = false
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
            buildConfigField("Boolean", "ENABLE_BETA_FEATURES", "false")
        }

        debug {
            isDebuggable = true
            buildConfigField("Boolean", "ENABLE_BETA_FEATURES", "false")
        }

        create("beta") {
            initWith(getByName("debug"))
            isDebuggable = true
            applicationIdSuffix = ".beta"
            versionNameSuffix = "-beta"
            buildConfigField("Boolean", "ENABLE_BETA_FEATURES", "true")
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }

    packaging {
        jniLibs {
            useLegacyPackaging = true
        }

        dex {
            useLegacyPackaging = true
        }

        resources {
            excludes += setOf(
                "DebugProbesKt.bin",
                "META-INF/androidx/annotation/annotation/LICENSE.txt",
                "META-INF/androidx/collection/collection/LICENSE.txt",
                "META-INF/androidx/collection/collection-ktx/LICENSE.txt",
                "META-INF/androidx/emoji2/emoji2/LICENSE.txt",
                "META-INF/androidx/lifecycle/lifecycle-common/LICENSE.txt",
                "META-INF/androidx/lifecycle/lifecycle-common-java8/LICENSE.txt",
            )
        }
    }

    lint {
        baseline = file("lint-baseline.xml")
        disable += listOf(
            "ChromeOsAbiSupport",
            "ObsoleteSdkInt",
        )
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

dependencies {
    implementation(libs.junit)
    implementation(libs.androidx.junit)
    implementation(libs.androidx.espresso.core)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.test.manifest)
    implementation(libs.androidx.compose.test.junit4)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    implementation(libs.androidx.core.splashscreen)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.io.coil.kt.coil3.compose)
    implementation(libs.io.coil.kt.coil3.network.okhttp)

    implementation(libs.topjohnwu.libsu.core)

    implementation(libs.composables.core)
}
