object Versions {
    const val minSDK = 23
    const val targetSDK = 29
    const val compileSDK = 29
    const val kotlin = "1.4.32"
    const val kotlinCoroutines = "1.4.1"
    const val koin = "2.1.5"
    const val compose = "1.0.0-beta07"
    const val colorPicker = "0.3.2-beta07"
    const val kaseChange = "1.3.0"
    const val activity = "1.3.0-alpha08"
    const val sqlDelight = "1.4.3"
    const val kodeHighlighter = "v3.0.0"
}

object Dependencies {
    const val colorPicker = "com.github.andb3:compose-color-picker:${Versions.colorPicker}"
    const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlinCoroutines}"

    object Activity {
        const val core = "androidx.activity:activity:${Versions.activity}"
        const val compose = "androidx.activity:activity-compose:${Versions.activity}"
        const val ktx = "androidx.activity:activity-ktx:${Versions.activity}"
    }

    object Compose {
        const val layout = "androidx.compose.foundation:foundation-layout:${Versions.compose}"
        const val material = "androidx.compose.material:material:${Versions.compose}"
        const val icons = "androidx.compose.material:material-icons-extended:${Versions.compose}"
        const val tooling = "androidx.compose.ui:ui-tooling:${Versions.compose}"
        const val compiler = "androidx.compose.compiler:compiler:${Versions.compose}"
    }

    object Koin {
        const val android = "org.koin:koin-android:${Versions.koin}"
        const val viewModel = "org.koin:koin-android-viewmodel:${Versions.koin}"
    }

    object KodeHighlighter {
        const val core = "com.github.markusressel.KodeHighlighter:core:${Versions.kodeHighlighter}"
        const val kotlin = "com.github.markusressel.KodeHighlighter:kotlin:${Versions.kodeHighlighter}"
    }

    object SQLDelight {
        const val android = "com.squareup.sqldelight:android-driver:${Versions.sqlDelight}"
        const val coroutines = "com.squareup.sqldelight:coroutines-extensions-jvm:${Versions.sqlDelight}"
    }
}