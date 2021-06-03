package com.andb.apps.composesandbox.ui.sandbox.drawer.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.andb.apps.composesandbox.state.Handler
import com.andb.apps.composesandbox.state.UserAction
import com.andb.apps.composesandbox.ui.sandbox.drawer.DrawerHeader
import com.andb.apps.composesandbox.ui.theme.MaterialThemeEditor
import com.andb.apps.composesandboxdata.model.Theme

@Composable
fun DrawerEditTheme(theme: Theme, onSelect: (Theme) -> Unit) {
    val actionHandler = Handler
    Column {
        DrawerHeader(title = "Edit Theme", onIconClick = { actionHandler.invoke(UserAction.Back)})
        MaterialThemeEditor(theme = theme, onSelect = onSelect)
    }
}

