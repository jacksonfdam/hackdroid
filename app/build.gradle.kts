import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

val jdkVersion = findProperty("jdkVersion").toString()

android {
    namespace = "com.hackdroid.demo"
    compileSdk = findProperty("android.compileSdk").toString().toInt()


    defaultConfig {
        applicationId = "com.hackdroid.demo"
        minSdk = findProperty("android.minSdk").toString().toInt()
        targetSdk = findProperty("android.targetSdk").toString().toInt()
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            // Intentionally debuggable for demo purposes
            isDebuggable = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(jdkVersion)
        targetCompatibility = JavaVersion.toVersion(jdkVersion)
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget(jdkVersion))
        }
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.core.ktx)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.navigation.compose)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.activity.compose)

    // For intentional vuln demos:
    implementation(libs.webkit)         // WebView
    implementation(libs.room.runtime)   // SQLite (for SQLi demo)
    implementation(libs.room.ktx)

    debugImplementation(libs.compose.ui.tooling)
}
