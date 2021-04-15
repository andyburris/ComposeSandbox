package com.andb.apps.composesandbox.ui.sandbox

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.andb.apps.composesandbox.BuildConfig
import com.andb.apps.composesandbox.state.*
import com.andb.apps.composesandbox.ui.common.ConfirmationDialog
import com.andb.apps.composesandbox.ui.common.MenuItem
import com.andb.apps.composesandbox.ui.common.OverflowMenu
import com.andb.apps.composesandboxdata.model.Project

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SandboxAppBar(sandboxState: ViewState.Sandbox, project: Project, iconState: BackdropValue, onToggle: () -> Unit) {
    val actionHandler = Handler
    TopAppBar(
        navigationIcon = {
            IconToggleButton(
                checked = iconState == BackdropValue.Revealed,
                onCheckedChange = { onToggle.invoke() }
            ) {
                when (iconState) {
                    BackdropValue.Concealed -> Icon(imageVector = Icons.Default.Menu, contentDescription = "Open Backdrop")
                    else -> Icon(imageVector = Icons.Default.Clear, contentDescription = "Close Backdrop")
                }
            }
        },
        title = {
            Box {
                BasicTextField(
                    value = project.name,
                    textStyle = LocalTextStyle.current.copy(color = LocalContentColor.current),
                    cursorBrush = SolidColor(LocalContentColor.current),
                    onValueChange = {
                        actionHandler.invoke(UserAction.UpdateProject(project.copy(name = it)))
                    },
                    decorationBox = { innerTextField ->
                        innerTextField()
                        if (project.name.isEmpty()) {
                            Text(
                                text = "Project Name",
                                style = MaterialTheme.typography.h6,
                                color = MaterialTheme.colors.onPrimary.copy(alpha = .12f)
                            )
                        }
                    }
                )
            }
        },
        actions = {
            IconButton(onClick = { actionHandler.invoke(UserAction.OpenDrawerScreen(DrawerScreen.EditTheme)) }) { Icon(imageVector = Icons.Default.Palette, contentDescription = "Open Theme Editor") }
            IconButton(onClick = { actionHandler.invoke(UserAction.OpenScreen(Screen.Preview(project.id, sandboxState.openedTree.id))) }) { Icon(imageVector = Icons.Default.PlayCircleFilled, contentDescription = "Play Prototype") }
            OverflowMenu {
                MenuItem(icon = Icons.Default.Share, title = "Export Code") {
                    val action = UserAction.OpenScreen(Screen.Code(project.id))
                    actionHandler.invoke(action)
                }

                if (BuildConfig.DEBUG) {
                    MenuItem(icon = Icons.Default.Build, title = "Test Screen") {
                        val action = UserAction.OpenScreen(Screen.Test)
                        actionHandler.invoke(action)
                    }
                }
                ConfirmationDialog { confirmationState ->
                    MenuItem(icon = Icons.Default.Delete, title = "Delete Project") {
                        confirmationState.confirm("Delete Project?", "You cannot undo this action") {
                            val action = UserAction.DeleteProject(sandboxState.project)
                            actionHandler.invoke(action)
                        }
                    }
                }
            }
        },
        elevation = 0.dp,
        backgroundColor = MaterialTheme.colors.primary
    )
}