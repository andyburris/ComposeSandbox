package com.andb.apps.composesandbox.ui.sandbox.drawer.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.andb.apps.composesandbox.state.Handler
import com.andb.apps.composesandbox.state.UserAction
import com.andb.apps.composesandbox.ui.common.MaterialThemeEditor
import com.andb.apps.composesandbox.ui.sandbox.drawer.DrawerHeader
import com.andb.apps.composesandboxdata.model.Theme

@Composable
fun DrawerEditTheme(theme: Theme, onSelect: (Theme) -> Unit) {
    val actionHandler = Handler
    Column {
        DrawerHeader(title = "Edit Theme", onIconClick = { actionHandler.invoke(UserAction.Back)})
        MaterialThemeEditor(theme = theme, modifier = Modifier.padding(horizontal = 32.dp), onSelect = onSelect)
    }
}

