package com.andb.apps.composesandbox.ui.code

import android.content.Intent
import androidx.compose.foundation.ScrollableRow
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.UnfoldLess
import androidx.compose.material.icons.filled.WrapText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ContextAmbient
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
                        Icon(asset = Icons.Default.ArrowBack)
                    }
                },
                title = { Text("Code Preview") },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(asset = Icons.Default.WrapText)
                    }
                }
            )
        },
        bodyContent = {
            CodeCard(screen = project.screens.first())
        },
        floatingActionButton = {
            val context = ContextAmbient.current
            ExtendedFloatingActionButton(
                icon = { Icon(asset = Icons.Default.Share) },
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

@Composable
private fun CodeCard(screen: PrototypeScreen) {
    Card(Modifier.padding(8.dp)) {
        Column(Modifier.padding(start = 16.dp, top = 16.dp, bottom = 16.dp)) {
            FileItem(screen = screen, modifier = Modifier.padding(end = 16.dp, bottom = 16.dp))
            Row {
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

@Composable
private fun FileItem(screen: PrototypeScreen, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Column {
            Text(text = screen.name)
            Text(text = "${screen.name.capitalize().filter { it != ' ' }}.kt", style = codeStyle)
        }
        IconButton(onClick = {}) {
            Icon(asset = Icons.Default.UnfoldLess)
        }
    }
}