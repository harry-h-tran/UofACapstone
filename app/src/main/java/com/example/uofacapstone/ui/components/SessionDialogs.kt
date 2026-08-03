package com.example.uofacapstone.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.uofacapstone.ui.viewmodels.MainViewModel

@Composable
fun SessionDialogs(
    mainViewModel: MainViewModel,
    onContinueClick: () -> Unit
) {
    val showContinueDialog by mainViewModel.showContinueDialog.collectAsState()
    val showSaveDialog by mainViewModel.showSaveDialog.collectAsState()

    SessionDialogsContent(
        showContinueDialog = showContinueDialog,
        showSaveDialog = showSaveDialog,
        onDismissContinue = { mainViewModel.dismissContinueDialog() },
        onConfirmContinue = { mainViewModel.confirmContinue(onContinueClick) },
        onDeclineContinue = { mainViewModel.declineContinue() },
        onDismissSave = { mainViewModel.dismissSaveDialog() },
        onConfirmSave = { mainViewModel.confirmSave() },
        onDeclineSave = { mainViewModel.declineSave() }
    )
}

@Composable
fun SessionDialogsContent(
    showContinueDialog: Boolean,
    showSaveDialog: Boolean,
    onDismissContinue: () -> Unit,
    onConfirmContinue: () -> Unit,
    onDeclineContinue: () -> Unit,
    onDismissSave: () -> Unit,
    onConfirmSave: () -> Unit,
    onDeclineSave: () -> Unit
) {
    if (showContinueDialog) {
        AlertDialog(
            onDismissRequest = onDismissContinue,
            title = { Text("Continue Session") },
            text = { Text("Do you want to continue tracking the current session?") },
            confirmButton = {
                TextButton(onClick = onConfirmContinue) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = onDeclineContinue) {
                    Text("No")
                }
            }
        )
    }

    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = onDismissSave,
            title = { Text("Save Session") },
            text = { Text("Would you like to save the incomplete session?") },
            confirmButton = {
                TextButton(onClick = onConfirmSave) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = onDeclineSave) {
                    Text("No")
                }
            }
        )
    }
}
