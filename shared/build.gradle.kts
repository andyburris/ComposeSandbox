plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("plugin.serialization") version "1.4.10"
    id("com.squareup.sqldelight")
}

android {
    compileSdkVersion(Versions.compileSDK)

    defaultConfig {
        minSdkVersion(Versions.minSDK)
        targetSdkVersion(Versions.targetSDK)

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.0")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.1.1")
    implementation(Dependencies.SQLDelight.android)
    implementation(Dependencies.SQLDelight.coroutines)
    implementation(Dependencies.Koin.android)
    implementation(Dependencies.coroutines)
    testImplementation("junit:junit:4.13.2")
}