object Versions {
    const val minSDK = 23
    const val targetSDK = 29
    const val compileSDK = 29
    const val kotlin = "1.4.0"
    const val koin = "2.1.5"
    const val compose = "1.0.0-alpha02"
}

object Dependencies {
    object Compose {
        val layout = "androidx.compose.foundation:foundation-layout:${Versions.compose}"
        val material = "androidx.compose.material:material:${Versions.compose}"
        val icons = "androidx.compose.material:material-icons-extended:${Versions.compose}"
        val tooling = "androidx.ui:ui-tooling:${Versions.compose}"
    }

    object Koin {
        const val android = "org.koin:koin-android:${Versions.koin}"
        const val viewModel = "org.koin:koin-android-viewmodel:${Versions.koin}"
    }
}