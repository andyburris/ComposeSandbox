object Versions {
    const val minSDK = 23
    const val targetSDK = 29
    const val compileSDK = 29
    const val kotlin = "1.4.0"
    const val koin = "2.1.5"
    const val compose = "1.0.0-alpha05"
    const val colorPicker = "0.1.0"
}

object Dependencies {
    const val colorPicker = "com.github.andb3:compose-color-picker:${Versions.colorPicker}"
    object Compose {
        const val layout = "androidx.compose.foundation:foundation-layout:${Versions.compose}"
        const val material = "androidx.compose.material:material:${Versions.compose}"
        const val icons = "androidx.compose.material:material-icons-extended:${Versions.compose}"
        const val tooling = "androidx.ui:ui-tooling:${Versions.compose}"
        const val compiler = "androidx.compose.compiler:compiler:${Versions.compose}"
    }

    object Koin {
        const val android = "org.koin:koin-android:${Versions.koin}"
        const val viewModel = "org.koin:koin-android-viewmodel:${Versions.koin}"
    }
}