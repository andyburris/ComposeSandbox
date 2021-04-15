// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        jcenter()
        mavenCentral()
        maven(url = "https://dl.bintray.com/kotlin/kotlin-eap")
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.0-alpha14")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}")
        classpath("com.squareup.sqldelight:gradle-plugin:${Versions.sqlDelight}")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven(url = "https://kotlin.bintray.com/kotlinx/") // soon will be just jcenter()
        maven(url = "https://dl.bintray.com/kotlin/kotlin-eap")
        maven(url = "https://jitpack.io")
    }
}
