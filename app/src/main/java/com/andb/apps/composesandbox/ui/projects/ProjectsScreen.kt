package com.andb.apps.composesandbox.ui.projects

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.andb.apps.composesandbox.state.Handler
import com.andb.apps.composesandbox.state.Screen
import com.andb.apps.composesandbox.state.UserAction
import com.andb.apps.composesandboxdata.model.Project

@Composable
fun ProjectsScreen(projects: List<Project>) {
    val handler = Handler
    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Add Project".toUpperCase()) },
                icon = { Icon(imageVector = Icons.Default.Add, contentDescription = null) },
                backgroundColor = MaterialTheme.colors.primary,
                onClick = {
                    handler.invoke(UserAction.OpenScreen(Screen.AddProject))
                }
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
            ProjectItem(
                project = project,
                modifier = Modifier.weight(1f).padding(16.dp).clickable {
                    val screen = Screen.Sandbox(project.id, project.trees.first().id)
                    println("screen = $screen")
                    handler.invoke(UserAction.OpenScreen(screen))
                }
            )
        }
    }
}

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


