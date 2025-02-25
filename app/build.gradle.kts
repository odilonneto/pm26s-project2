plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services") // Plugin do Google Services
}

android {
    namespace = "com.example.pm26sproject2"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.pm26sproject2"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Firebase libraries via BOM
    implementation(platform(libs.firebase.bom))

    // Firebase Auth (KTX version, already managed by BOM)
    implementation(libs.google.firebase.auth.ktx)
    // Firebase Firestore (KTX version, already managed by BOM)
    implementation(libs.firebase.firestore.ktx)
    // Firebase Database (KTX version, already managed by BOM)
    implementation(libs.google.firebase.database.ktx)

    // Play Services Auth
    implementation(libs.play.services.auth)

    // AndroidX Libraries
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.cardview)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // Test Libraries
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
