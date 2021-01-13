package com.andb.apps.composesandbox.ui.common

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
fun ConfirmationDialog(
    content: @Composable (ConfirmationDialogState) -> Unit,
) {
    val title = remember { mutableStateOf("") }
    val summary = remember { mutableStateOf("") }
    val (dialogShowing, setDialogShowing) = remember { mutableStateOf(false) }
    val confirmationDialogState = remember { ConfirmationDialogState { newTitle, newSummary ->
        title.value = newTitle
        summary.value = newSummary
        setDialogShowing(true)
    } }
    content.invoke(confirmationDialogState)
    if (dialogShowing) {
        AlertDialog(
            onDismissRequest = { setDialogShowing.invoke(false) },
            title = { Text(text = title.value) },
            text = { Text(text = summary.value) },
            confirmButton = {
                TextButton(
                    onClick = {
                        setDialogShowing.invoke(false)
                        confirmationDialogState.confirm()
                    }
                ) {
                    Text(text = "OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { setDialogShowing.invoke(false) }) {
                    Text(text = "CANCEL")
                }
            }
        )
    }
}

data class ConfirmationDialogState(private val onNeedToConfirm: (title: String, summary: String) -> Unit) {
    private var currentConfirmBlock: () -> Unit = {}
    fun confirm(title: String, summary: String, needToConfirm: Boolean = true, onConfirm: () -> Unit ) {
        if (needToConfirm) {
            currentConfirmBlock = onConfirm
            onNeedToConfirm.invoke(title, summary)
        } else {
            onConfirm.invoke()
        }
    }
    internal fun confirm() {
        currentConfirmBlock.invoke()
    }
}