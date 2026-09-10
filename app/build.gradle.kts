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
        versionName = "0.2.0"
    }

    androidResources {
        noCompress += "tflite"
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("org.tensorflow:tensorflow-lite:2.17.0")
}
