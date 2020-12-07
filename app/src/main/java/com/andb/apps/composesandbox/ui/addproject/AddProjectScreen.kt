package com.andb.apps.composesandbox.ui.addproject

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.andb.apps.composesandbox.model.*
import com.andb.apps.composesandbox.model.Properties
import com.andb.apps.composesandbox.ui.projects.LazyGridFor
import com.andb.apps.composesandbox.ui.projects.ProjectItem
import java.util.*

private val templates = listOf(
    //******** Empty *******//
    Project(
        name = "Empty",
        screens = listOf(
            PrototypeComponent.Group.Column()
        ),
        theme = lightColors().toTheme()
    ),

    //******** Hello World *******//
    Project(
        name = "Hello World",
        screens = listOf(
            PrototypeComponent.Group.Column(
                properties = Properties.Group.Column(
                    horizontalAlignment = PrototypeAlignment.Horizontal.CenterHorizontally,
                    verticalArrangement = PrototypeArrangement.Vertical.Center,
                ),
                modifiers = listOf(PrototypeModifier.FillMaxHeight(), PrototypeModifier.FillMaxWidth()),
                children = listOf(
                    PrototypeComponent.Text(
                        properties = Properties.Text("Hello World")
                    )
                )
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
                    Icon(asset = Icons.Default.Add)
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
            LazyGridFor(
                items = templates,
                columns = 2,
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                header = {
                    Column(Modifier.padding(horizontal = 16.dp)) {
                        Text(
                            text = "Add Project",
                            style = MaterialTheme.typography.h4,
                            modifier = Modifier.padding(vertical = 32.dp)
                        )
                        OutlinedTextField(
                            value = name.value,
                            onValueChange = { name.value = it },
                            label = {
                                Text(text = "Name")
                            },
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                        )
                    }
                }
            ) { project ->
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
    )

}