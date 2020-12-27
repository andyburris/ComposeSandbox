package com.andb.apps.composesandbox.ui.code

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animate
import androidx.compose.foundation.ScrollableRow
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.UnfoldLess
import androidx.compose.material.icons.filled.WrapText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.andb.apps.composesandbox.model.Project
import com.andb.apps.composesandbox.model.PrototypeScreen
import com.andb.apps.composesandbox.model.toCode
import com.andb.apps.composesandbox.state.Handler
import com.andb.apps.composesandbox.state.UserAction
import com.andb.apps.composesandbox.util.endBorder

private val codeStyle = TextStyle(fontFamily = FontFamily.Monospace)

@Composable
fun CodeScreen(project: Project) {
    val actionHandler = Handler
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { actionHandler.invoke(UserAction.Back) }) {
                        Icon(imageVector = Icons.Default.ArrowBack)
                    }
                },
                title = { Text("Code Preview") },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(imageVector = Icons.Default.WrapText)
                    }
                },
                backgroundColor = MaterialTheme.colors.primary
            )
        },
        bodyContent = {
            val opened = remember { mutableStateOf<String?>(null) }
            LazyColumn {
                items(project.screens) {
                    CodeCard(screen = it, opened = opened.value == it.id) {
                        opened.value = if (opened.value == it.id) null else it.id
                    }
                }
            }
        },
        floatingActionButton = {
            val context = AmbientContext.current
            ExtendedFloatingActionButton(
                icon = { Icon(imageVector = Icons.Default.Share) },
                text = { Text(text = "Export Code") },
                onClick = {
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, project.screens.first().tree.toCode())
                        type = "text/plain"
                    }

                    val shareIntent = Intent.createChooser(sendIntent, null)
                    context.startActivity(shareIntent)
                },
                backgroundColor = MaterialTheme.colors.primary
            )
        }
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun CodeCard(screen: PrototypeScreen, opened: Boolean, modifier: Modifier = Modifier, onToggle: () -> Unit) {
    val padding = animate(if (opened) 8.dp else 0.dp)
    val elevation = animate(if (opened) 4.dp else 0.dp)
    Card(modifier.padding(padding), elevation = elevation, shape = RoundedCornerShape(padding)) {
        Column {
            FileItem(screen = screen, onToggle = onToggle)
            AnimatedVisibility(visible = opened) {
                Row(Modifier.padding(start = 16.dp, bottom = 16.dp)) {
                    val code = screen.tree.toCode()
                    Text(
                        text = (1..code.lines().size).joinToString("\n"),
                        style = codeStyle.copy(textAlign = TextAlign.End),
                        color = MaterialTheme.colors.onSecondary,
                        modifier = Modifier.endBorder(1.dp, MaterialTheme.colors.secondary).padding(end = 8.dp)
                    )
                    ScrollableRow {
                        Text(text = code, style = codeStyle, modifier = Modifier.padding(start = 8.dp, end = 16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun FileItem(screen: PrototypeScreen, modifier: Modifier = Modifier, onToggle: () -> Unit) {
    Row(modifier.clickable(onClick = onToggle).padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Column {
            Text(text = screen.name)
            Text(text = "${screen.name.capitalize().filter { it != ' ' }}.kt", style = codeStyle)
        }
        Icon(imageVector = Icons.Default.UnfoldLess)
    }
}