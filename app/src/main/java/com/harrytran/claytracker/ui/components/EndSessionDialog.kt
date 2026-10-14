package com.harrytran.claytracker.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun EndSessionDialog(
    showEndSessionDialog: Boolean,
    showSaveDialog: Boolean,
    showSummaryDialog: Boolean = false,
    totalHits: Int = 0,
    totalShots: Int = 0,
    onDismissEndSession: () -> Unit,
    onConfirmEndSession: () -> Unit,
    onDismissSaveDialog: () -> Unit,
    onConfirmSave: () -> Unit,
    onConfirmErase: () -> Unit,
    onDismissSummary: () -> Unit = {},
    endSessionTitle: String = "End Session",
    endSessionText: String = "Would you like to end this session?",
    confirmButtonText: String = "Yes",
    dismissButtonText: String = "No",
    onCancelEndSession: () -> Unit = onDismissEndSession
) {
    if (showEndSessionDialog) {
        AlertDialog(
            onDismissRequest = onCancelEndSession,
            title = { Text(endSessionTitle) },
            text = { Text(endSessionText) },
            confirmButton = {
                TextButton(onClick = onConfirmEndSession) {
                    Text(confirmButtonText)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissEndSession) {
                    Text(dismissButtonText)
                }
            }
        )
    }

    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = onDismissSaveDialog,
            title = { Text("Save Session") },
            text = { Text("Would you like to Save the session data?") },
            confirmButton = {
                TextButton(onClick = onConfirmSave) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = onConfirmErase) {
                    Text("No")
                }
            }
        )
    }

    if (showSummaryDialog) {
        val accuracy = if (totalShots > 0) (totalHits.toFloat() / totalShots * 100).toInt() else 0
        AlertDialog(
            onDismissRequest = onDismissSummary,
            title = { Text("Session Saved") },
            text = {
                Column {
                    Text("Score: $totalHits")
                    Text("Hits / Misses:")
                    Text("$totalHits / ${totalShots - totalHits}")
                    Text("Accuracy: $accuracy%")
                }
            },
            confirmButton = {
                TextButton(onClick = onDismissSummary) {
                    Text("Close")
                }
            }
        )
    }
}
