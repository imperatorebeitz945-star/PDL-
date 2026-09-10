plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.pdl.childsafety"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.pdl.childsafety"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"
    }

    buildFeatures {
        viewBinding = false
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}
