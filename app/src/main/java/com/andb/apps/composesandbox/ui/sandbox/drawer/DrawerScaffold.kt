package com.andb.apps.composesandbox.ui.sandbox.drawer

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.AmbientDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex


@Composable
fun DrawerHeader(
    title: String,
    modifier: Modifier = Modifier,
    titleSlot: @Composable (title: String) -> Unit = { Text(text = title, style = MaterialTheme.typography.h6, maxLines = 1, overflow = TextOverflow.Ellipsis) },
    icon: ImageVector = Icons.Default.ArrowBack,
    screenName: String? = null,
    iconSlot: @Composable (icon: ImageVector) -> Unit = {
        IconButton(onClick = onIconClick) {
            Icon(imageVector = it)
        }
    },
    onIconClick: () -> Unit,
    actions: (@Composable RowScope.() -> Unit) = {}
) {
    TopAppBar (
        title = {
            Column(Modifier.offset(x = (-8).dp)) {
                if (screenName != null) {
                    Text(text = screenName, style = MaterialTheme.typography.overline, color = MaterialTheme.colors.onSecondary)
                }
                titleSlot(title)
            }
        },
        navigationIcon = {
            iconSlot(icon)
        },
        actions = {
            actions()
        },
        modifier = modifier.padding(vertical = 16.dp, horizontal = 16.dp).fillMaxWidth(),
        elevation = 0.dp,
        backgroundColor = Color.Transparent
    )
}

@Composable
fun ScrollableDrawer(header: @Composable () -> Unit, content: @Composable ColumnScope.() -> Unit) {
    val scrollState = rememberScrollState()
    Column {
        Box(
            modifier = Modifier
                .zIndex(4f)
                .shadow(scrollState.toShadow())
                .background(AmbientElevationOverlay.current?.apply(color = MaterialTheme.colors.surface, elevation = AmbientAbsoluteElevation.current + scrollState.toShadow()) ?: MaterialTheme.colors.surface)
        ) {
            header()
        }
        ScrollableColumn(scrollState = scrollState) {
            content()
        }
    }
}

@Composable
fun ScrollState.toShadow() = with(AmbientDensity.current){ this@toShadow.value.toDp() }.coerceAtMost(4.dp)