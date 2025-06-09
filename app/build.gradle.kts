import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.rve.rvkernelmanager"
    compileSdk = 35
    buildToolsVersion = "35.0.1"
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
        minSdk = 29
        targetSdk = 35
        versionCode = 11
        versionName = "1.1"
        
        vectorDrawables { 
            useSupportLibrary = true
        }

        ndk {
            abiFilters += listOf("arm64-v8a", "armeabi-v7a")
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }

    kotlinOptions {
        jvmTarget = "21"
    }

    packaging {
        jniLibs {
            useLegacyPackaging = true
        }

	dex {
	    useLegacyPackaging = true
	}

        resources.excludes += "DebugProbesKt.bin"
    }
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.compose.bom)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    implementation(libs.topjohnwu.libsu.core)
}
