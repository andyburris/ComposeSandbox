package com.andb.apps.composesandbox.ui.projects

import androidx.compose.foundation.Icon
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumnFor
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
fun ProjectsScreen(projects: List<Project>){
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
        ScrollableColumn(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "Compose Sandbox",
                style = MaterialTheme.typography.h4,
                modifier = Modifier.padding(vertical = 32.dp, horizontal = 32.dp)
            )
            LazyGridFor(items = projects, modifier = Modifier.padding(horizontal = 16.dp), columns = 2) { project ->
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
}

@Composable
fun <T> LazyGridFor(items: List<T>, columns: Int, modifier: Modifier = Modifier, itemContent: @Composable() (T) -> Unit) {
    LazyColumnFor(items = items.chunked(columns), modifier = modifier) { rowItems ->
        Row {
            rowItems.forEach {
                itemContent(it)
            }
            repeat (columns - rowItems.size) {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

