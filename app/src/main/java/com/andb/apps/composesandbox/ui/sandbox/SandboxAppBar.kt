package com.andb.apps.composesandbox.ui.sandbox

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.andb.apps.composesandbox.BuildConfig
import com.andb.apps.composesandbox.data.model.name
import com.andb.apps.composesandbox.state.*
import com.andb.apps.composesandbox.ui.common.ConfirmationDialog
import com.andb.apps.composesandbox.ui.common.MenuItem
import com.andb.apps.composesandbox.ui.common.OverflowMenu
import com.andb.apps.composesandbox.util.onBackgroundSecondary
import com.andb.apps.composesandbox.util.startBorder
import com.andb.apps.composesandboxdata.local.HistoryEntry
import com.andb.apps.composesandboxdata.model.Project
import com.andb.apps.composesandboxdata.model.TreeType
import com.andb.apps.composesandboxdata.state.ProjectAction
import kotlinx.datetime.*
import java.time.format.DateTimeFormatter
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SandboxAppBar(sandboxState: ViewState.Sandbox, project: Project, iconState: BackdropValue, onToggle: () -> Unit, onUpdateProject: (UserAction.UpdateProject) -> Unit) {
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
                        onUpdateProject.invoke(UserAction.UpdateProject(project, ProjectAction.UpdateName(it)))
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
                MenuItem(icon = Icons.Default.Undo, title = "Undo", enabled = sandboxState.project.pastHistory.isNotEmpty()) {
                    val action = UserAction.Undo(sandboxState.project)
                    actionHandler.invoke(action)
                }
                MenuItem(icon = Icons.Default.Redo, title = "Redo", enabled = sandboxState.project.futureHistory.isNotEmpty()) {
                    val action = UserAction.Redo(sandboxState.project)
                    actionHandler.invoke(action)
                }
                val showingDialog = remember { mutableStateOf(false) }
                MenuItem(icon = Icons.Default.History, title = "Version History") {
                    showingDialog.value = true
                }
                if (showingDialog.value) {
                    Dialog(onDismissRequest = { showingDialog.value = false }) {
                        Surface(shape = MaterialTheme.shapes.medium) {
                            Column {
                                Row(modifier = Modifier.padding(32.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    Icon(imageVector = Icons.Default.Clear, contentDescription = "Dismiss", modifier = Modifier.clickable { showingDialog.value = false }, tint = MaterialTheme.colors.onBackgroundSecondary)
                                    Text(text = "Version History", style = MaterialTheme.typography.h6, color = MaterialTheme.colors.onBackgroundSecondary)
                                }
                                VersionHistory(pastHistory = sandboxState.project.pastHistory, futureHistory = sandboxState.project.futureHistory, modifier = Modifier.fillMaxWidth())
                            }
                        }
                    }
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

@Composable
fun VersionHistory(pastHistory: List<HistoryEntry>, futureHistory: List<HistoryEntry>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        items(futureHistory.sortedByDescending { it.time }) {
            HistoryItem(
                entry = it,
                modifier = Modifier.padding(vertical = 12.dp, horizontal = 32.dp)
            )
        }
        if (pastHistory.isNotEmpty()) {
            item {
                HistoryItem(
                    entry = pastHistory.last(),
                    modifier = Modifier
                        .startBorder(2.dp, MaterialTheme.colors.primary)
                        .background(MaterialTheme.colors.primary.copy(alpha = .12f))
                        .padding(vertical = 12.dp, horizontal = 32.dp)
                )
            }
            items(pastHistory.dropLast(1).sortedByDescending { it.time }) {
                HistoryItem(
                    entry = it,
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 32.dp)
                )
            }
        }
    }
}

@Composable
fun HistoryItem(entry: HistoryEntry, modifier: Modifier = Modifier) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = modifier.fillMaxWidth()) {
        Icon(imageVector = entry.action.icon, contentDescription = entry.action.name, tint = MaterialTheme.colors.onBackgroundSecondary)
        Column {
            Text(text = entry.action.name, style = MaterialTheme.typography.subtitle1)
            Text(text = entry.time.formatString(), style = MaterialTheme.typography.body1, color = MaterialTheme.colors.onBackgroundSecondary)
        }
    }
}

@OptIn(ExperimentalTime::class)
fun LocalDateTime.formatString(): String {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val todayStart = now.date.atStartOfDayIn(TimeZone.currentSystemDefault())
    val yesterdayStart = now.date.minus(DateTimeUnit.DAY).atStartOfDayIn(TimeZone.currentSystemDefault())
    val weekStart = now.date.minus(DateTimeUnit.WEEK).atStartOfDayIn(TimeZone.currentSystemDefault())
    val yearStart =  now.date.minus(DateTimeUnit.YEAR).atStartOfDayIn(TimeZone.currentSystemDefault())
    return when(val instant = this.toInstant(TimeZone.currentSystemDefault())) {
        in todayStart..now.toInstant(TimeZone.currentSystemDefault()) -> instant.format("h:mm a")
        in yesterdayStart..todayStart -> "Yesterday, " + instant.format("h:mm a")
        in weekStart..yesterdayStart -> instant.format("E, h:mm a")
        in yearStart..weekStart -> instant.format("M/dd, hh:mm a")
        else -> instant.format("M/dd/yyyy, hh:mm a")
    }
}

fun Instant.format(pattern: String) = DateTimeFormatter.ofPattern(pattern).format(this.toLocalDateTime(TimeZone.currentSystemDefault()).toJavaLocalDateTime())

val ProjectAction.name @Composable get() = when(this) {
    is ProjectAction.AddTree -> "Added ${if (tree.treeType == TreeType.Component) "Component" else "Screen"}: ${tree.name}"
    is ProjectAction.DeleteTree -> "Deleted ${if (tree.treeType == TreeType.Component) "Component" else "Screen"}: ${tree.name}"
    is ProjectAction.ExtractComponent -> "Extracted Component: ${tree.name}"
    is ProjectAction.TreeAction.AddComponent -> "Added ${adding.name}"
    is ProjectAction.TreeAction.DeleteComponent -> "Deleted ${deleting.name}"
    is ProjectAction.TreeAction.MoveComponent -> "Moved ${moving.name}"
    is ProjectAction.TreeAction.UpdateComponent -> "Updated ${component.name}"
    is ProjectAction.TreeAction.UpdateName -> "Renamed ${tree.name} to $name"
    is ProjectAction.UpdateName -> "Renamed project to $name"
    is ProjectAction.UpdateTheme -> "Updated Theme"
}

val ProjectAction.icon @Composable get() = when(this) {
    is ProjectAction.AddTree -> Icons.Default.Add
    is ProjectAction.DeleteTree -> Icons.Default.DeleteSweep
    is ProjectAction.ExtractComponent -> Icons.Default.ControlPointDuplicate
    is ProjectAction.TreeAction.AddComponent -> Icons.Default.Add
    is ProjectAction.TreeAction.DeleteComponent -> Icons.Default.Delete
    is ProjectAction.TreeAction.MoveComponent -> Icons.Default.Redo
    is ProjectAction.TreeAction.UpdateComponent -> Icons.Default.Update
    is ProjectAction.TreeAction.UpdateName -> Icons.Default.Edit
    is ProjectAction.UpdateName -> Icons.Default.Edit
    is ProjectAction.UpdateTheme -> Icons.Default.Palette
}