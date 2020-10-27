package com.andb.apps.composesandbox.ui.projects

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.ExperimentalLazyDsl
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.andb.apps.composesandbox.data.model.Project
import com.andb.apps.composesandbox.state.Handler
import com.andb.apps.composesandbox.state.SandboxState
import com.andb.apps.composesandbox.state.Screen
import com.andb.apps.composesandbox.state.UserAction

@Composable
fun ProjectsScreen(projects: List<Project>) {
    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Add Project".toUpperCase()) },
                icon = { Icon(asset = Icons.Default.Add) },
                backgroundColor = MaterialTheme.colors.primary,
                onClick = {}
            )
        }
    ) {
        LazyGridFor(
            items = projects,
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            columns = 2,
            header = {
                Text(
                    text = "Compose Sandbox",
                    style = MaterialTheme.typography.h4,
                    modifier = Modifier.padding(vertical = 32.dp, horizontal = 16.dp)
                )
            }
        ) { project ->
            val handler = Handler
            ProjectItem(
                project = project,
                modifier = Modifier.weight(1f).padding(16.dp).clickable {
                    val screen = Screen.Sandbox(SandboxState(project))
                    println("screen = $screen")
                    handler.invoke(UserAction.OpenScreen(screen))
                }
            )
        }
    }
}

@OptIn(ExperimentalLazyDsl::class)
@Composable
fun <T> LazyGridFor(items: List<T>, columns: Int, modifier: Modifier = Modifier, header: (@Composable() LazyItemScope.() -> Unit)? = null, itemContent: @Composable() RowScope.(T) -> Unit) {
    LazyColumn(modifier = modifier) {
        if (header != null) {
            item(header)
        }
        items(items.chunked(columns)) { rowItems ->
            Row {
                rowItems.forEach {
                    itemContent(it)
                }
                repeat(columns - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

