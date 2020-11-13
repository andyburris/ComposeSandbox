package com.andb.apps.composesandbox.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.staticAmbientOf
import com.andb.apps.composesandbox.data.model.Theme

val ProjectThemeAmbient = staticAmbientOf<Theme>()

@Composable
val ProjectTheme get() = ProjectThemeAmbient.current

@Composable
fun ProjectThemeProvider(projectTheme: Theme, content: @Composable() () -> Unit){
    Providers(ProjectThemeAmbient provides projectTheme) {
        content()
    }
}
