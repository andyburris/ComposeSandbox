package com.andb.apps.composesandbox.ui.addproject

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.dp
import com.andb.apps.composesandbox.data.model.toTheme
import com.andb.apps.composesandbox.ui.projects.ProjectItem
import com.andb.apps.composesandbox.util.gridItems
import com.andb.apps.composesandboxdata.model.*
import com.andb.apps.composesandboxdata.model.Properties
import java.util.*

private val templates = listOf(
    //******** Empty *******//
    Project(
        name = "Empty",
        trees = listOf(
            PrototypeTree(name = "Screen 1", treeType = TreeType.Screen)
        ),
        theme = lightColors().toTheme()
    ),

    //******** Hello World *******//
    Project(
        name = "Hello World",
        trees = listOf(
            PrototypeTree(
                name = "Screen 1",
                component = PrototypeComponent.Group.Column(
                    properties = Properties.Group.Column(
                        horizontalAlignment = PrototypeAlignment.Horizontal.CenterHorizontally,
                        verticalArrangement = PrototypeArrangement.Vertical.Center,
                    ),
                    modifiers = listOf(PrototypeModifier.FillMaxSize()),
                    children = listOf(
                        PrototypeComponent.Text(
                            properties = Properties.Text("Hello World")
                        )
                    )
                ),
                treeType = TreeType.Screen
            )
        ),
        theme = lightColors().toTheme()
    ),

)

@Composable
fun AddProjectScreen(onAddProject: (Project) -> Unit) {
    val name = savedInstanceState { "" }
    val selectedTemplateID = savedInstanceState<String?> { null }
    val canSave = name.value.isNotEmpty() && selectedTemplateID.value != null
    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                backgroundColor = if (canSave) MaterialTheme.colors.primary else MaterialTheme.colors.primary.copy(alpha = .25f).compositeOver(Color.White),
                text = {
                    Text(text = "Create Project")
                },
                icon = {
                    Icon(imageVector = Icons.Default.Add)
                },
                onClick = {
                    if (canSave) {
                        val project = templates.first { it.id == selectedTemplateID.value }.copy(
                            id = UUID.randomUUID().toString(), //need a new id every time
                            name = name.value
                        )
                        onAddProject.invoke(project)
                    }
                },
            )
        },
        bodyContent = {
            LazyColumn(
                Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(32.dp),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 32.dp, bottom = 32.dp)
            ) {
                item {
                    Text(
                        text = "Add Project",
                        style = MaterialTheme.typography.h4,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                item {
                    OutlinedTextField(
                        value = name.value,
                        onValueChange = { name.value = it },
                        label = {
                            Text(text = "Name")
                        },
                        modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth()
                    )
                }

                item {
                    Text(
                        text = "TEMPLATES",
                        style = MaterialTheme.typography.subtitle1,
                        color = MaterialTheme.colors.primary,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                gridItems(templates, 2) { project ->
                    ProjectItem(
                        project = project,
                        selected = selectedTemplateID.value == project.id,
                        modifier = Modifier
                            .weight(1f)
                            .padding(16.dp)
                            .clickable {
                                if (name.value.isEmpty()) {
                                    name.value = project.name
                                }
                                selectedTemplateID.value = project.id
                            }
                    )
                }
            }
        }
    )

}