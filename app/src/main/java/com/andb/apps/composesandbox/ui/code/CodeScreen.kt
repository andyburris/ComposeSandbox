package com.andb.apps.composesandbox.ui.code

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animate
import androidx.compose.foundation.ScrollableRow
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.core.content.FileProvider
import com.andb.apps.composesandbox.state.Handler
import com.andb.apps.composesandbox.state.UserAction
import com.andb.apps.composesandbox.util.endBorder
import com.andb.apps.composesandboxdata.model.*

private val codeStyle = TextStyle(fontFamily = FontFamily.Monospace)

@Composable
fun CodeScreen(project: Project) {
    val actionHandler = Handler
    val generator = remember(project) { CodeGenerator(project) }
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { actionHandler.invoke(UserAction.Back) }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                title = { Text("Code Preview") },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(imageVector = Icons.Default.WrapText, contentDescription = "Wrap Code")
                    }
                },
                backgroundColor = MaterialTheme.colors.primary
            )
        },
        bodyContent = {
            val opened = remember { mutableStateOf<String?>(null) }
            LazyColumn {
                item {
                    Text(text = "SCREENS", style = MaterialTheme.typography.subtitle1, color = MaterialTheme.colors.primary, modifier = Modifier.padding(16.dp))
                }
                val (screens, components) = project.trees.partition { it.treeType == TreeType.Screen }
                items(screens) {
                    CodeCard(generator = generator, tree = it, opened = opened.value == it.id) {
                        opened.value = if (opened.value == it.id) null else it.id
                    }
                }
                item {
                    Text(text = "CUSTOM COMPONENTS", style = MaterialTheme.typography.subtitle1, color = MaterialTheme.colors.primary, modifier = Modifier.padding(16.dp))
                }
                items(components) {
                    CodeCard(generator = generator, tree = it, opened = opened.value == it.id) {
                        opened.value = if (opened.value == it.id) null else it.id
                    }
                }
            }
        },
        floatingActionButton = {
            val context = AmbientContext.current
            ExtendedFloatingActionButton(
                icon = { Icon(imageVector = Icons.Default.Share, contentDescription = null) },
                text = { Text(text = "Export Code") },
                onClick = {
                    val zipFile = project.exportZip(context)
                    val sendIntent: Intent = Intent().apply {
                        val uri = FileProvider.getUriForFile(context, context.packageName + ".fileprovider", zipFile)
                        println("exporting $uri")
                        action = Intent.ACTION_SEND
                        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                        putExtra(Intent.EXTRA_STREAM, uri)
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
private fun CodeCard(generator: CodeGenerator, tree: PrototypeTree, opened: Boolean, modifier: Modifier = Modifier, onToggle: () -> Unit) {
    val padding = animate(if (opened) 8.dp else 0.dp)
    val elevation = animate(if (opened) 4.dp else 0.dp)
    Card(modifier.padding(padding), elevation = elevation, shape = RoundedCornerShape(padding)) {
        Column {
            FileItem(tree = tree, onToggle = onToggle, expanded = opened)
            AnimatedVisibility(visible = opened) {
                Row(Modifier.padding(start = 16.dp, bottom = 16.dp)) {
                    val code = with(generator) { tree.toCode() }
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
private fun FileItem(tree: PrototypeTree, modifier: Modifier = Modifier, expanded: Boolean, onToggle: () -> Unit) {
    Row(modifier.clickable(onClick = onToggle).padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Column {
            Text(text = tree.name, style = MaterialTheme.typography.subtitle1)
            Text(text = "${tree.name.capitalize().filter { it != ' ' }}.kt", style = codeStyle)
        }
        when (expanded) {
            true -> Icon(imageVector = Icons.Default.UnfoldLess, contentDescription = "Collapse")
            false -> Icon(imageVector = Icons.Default.UnfoldMore, contentDescription = "Expand")
        }
    }
}