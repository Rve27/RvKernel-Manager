import java.util.Properties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.rve.rvkernelmanager"
    compileSdk = 36
    buildToolsVersion = "36.0.0"
    ndkVersion = "28.1.13356709"

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
                properties?.getProperty(propertyName) ?: System.getenv(environmentName) ?:
                System.console()?.readLine("\n$prompt: ")
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
        versionCode = 3
        versionName = "1.2.1"
        
        vectorDrawables { 
            useSupportLibrary = true
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
	    isCrunchPngs = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }
    }

    splits {
        abi {
            isEnable = true
	    isUniversalApk = true
            reset()
            include("arm64-v8a", "armeabi-v7a")
        }
    }

    bundle {
        language {
            enableSplit = true
        }
        density {
            enableSplit = true
        }
        abi {
            enableSplit = true
        }
    }

    buildFeatures {
        compose = true
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
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE",
                "META-INF/LICENSE.txt",
                "META-INF/license.txt",
                "META-INF/NOTICE",
                "META-INF/NOTICE.txt",
                "META-INF/notice.txt",
                "META-INF/ASL2.0",
                "META-INF/AL2.0",
                "META-INF/LGPL2.1",
                "META-INF/*.kotlin_module",
                "**/attach_hotspot_windows.dll"
            )
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

dependencies {
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.topjohnwu.libsu.core)
}
