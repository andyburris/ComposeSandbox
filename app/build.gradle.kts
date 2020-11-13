plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    compileSdkVersion(Versions.compileSDK)
    buildToolsVersion = "30.0.0"

    defaultConfig {
        applicationId = "com.andb.apps.composesandbox"
        minSdkVersion(Versions.minSDK)
        targetSdkVersion(Versions.targetSDK)
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            minifyEnabled(false)
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        apiVersion = "1.4"
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Versions.compose
        kotlinCompilerVersion = "1.4.0"
    }
}

dependencies {

    implementation("org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}")
    implementation("androidx.core:core-ktx:1.3.1")
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("com.google.android.material:material:1.2.1")
    implementation(Dependencies.Compose.layout)
    implementation(Dependencies.Compose.material)
    implementation(Dependencies.Compose.icons)
    implementation(Dependencies.Compose.tooling)
    implementation(Dependencies.Compose.compiler)
    implementation(Dependencies.Koin.android)
    implementation(Dependencies.Koin.viewModel)
    implementation(Dependencies.colorPicker)
    testImplementation("junit:junit:4.13")
    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
}

tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class.java).configureEach {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs += arrayOf("-Xallow-jvm-ir-dependencies", "-Xskip-prerelease-check")
    }
}
