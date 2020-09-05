package com.andb.apps.composesandbox.ui.projects

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.foundation.Icon
import androidx.ui.foundation.ScrollableColumn
import androidx.ui.foundation.Text
import androidx.ui.foundation.drawBackground
import androidx.ui.foundation.lazy.LazyColumnItems
import androidx.ui.graphics.Color
import androidx.ui.layout.*
import androidx.ui.material.ExtendedFloatingActionButton
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Scaffold
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Add
import androidx.ui.unit.dp
import com.andb.apps.composesandbox.data.model.Project
import com.andb.apps.composesandbox.state.ActionHandlerAmbient
import com.andb.apps.composesandbox.state.Handler
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
            LazyGridItems(items = projects, modifier = Modifier.padding(horizontal = 32.dp), columns = 2) { project ->
                val handler = Handler
                ProjectItem(
                    project = project,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val screen = Screen.Sandbox(it)
                        println("screen = $screen")
                        handler.invoke(UserAction.OpenScreen(screen))
                    }
                )
            }
        }
    }
}

@Composable
fun <T> LazyGridItems(items: List<T>, columns: Int, modifier: Modifier = Modifier, itemContent: @Composable() (T) -> Unit) {
    LazyColumnItems(items = items.chunked(columns), modifier = modifier) { rowItems ->
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

