package com.example.uofacapstone.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.uofacapstone.ui.theme.UofACapstoneTheme
import com.example.uofacapstone.ui.viewmodels.FreeShootViewModel
import com.example.uofacapstone.ui.viewmodels.FreeShootUiState
import com.example.uofacapstone.ui.components.EndSessionDialog
import com.example.uofacapstone.ui.components.HitMissButtons
import com.example.uofacapstone.ui.components.SessionEndTitleBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewFreeShootScreen(
    viewModel: FreeShootViewModel = viewModel(),
    onBack: () -> Unit,
    onSessionFinished: (Long) -> Unit
) {
    BackHandler(onBack = onBack)
    val uiState by viewModel.uiState.collectAsState()

    NewFreeShootContent(
        uiState = uiState,
        onHit = { viewModel.onHit() },
        onMiss = { viewModel.onMiss() },
        onStopSession = { viewModel.onStopSession() },
        onDismissEndSession = { viewModel.dismissEndSessionDialog() },
        onConfirmEndSession = { viewModel.confirmEndSession() },
        onDismissSaveDialog = { viewModel.dismissSaveDialog() },
        onConfirmSave = { viewModel.saveSession() },
        onConfirmErase = { viewModel.eraseSession(onBack) },
        onDismissSummary = { viewModel.dismissSummaryDialog(onSessionFinished) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewFreeShootContent(
    uiState: FreeShootUiState,
    onHit: () -> Unit,
    onMiss: () -> Unit,
    onStopSession: () -> Unit,
    onDismissEndSession: () -> Unit,
    onConfirmEndSession: () -> Unit,
    onDismissSaveDialog: () -> Unit,
    onConfirmSave: () -> Unit,
    onConfirmErase: () -> Unit,
    onDismissSummary: () -> Unit
) {
    val primaryBlue = Color(0xFF1B3D6C)

    EndSessionDialog(
        showEndSessionDialog = uiState.showEndSessionDialog,
        showSaveDialog = uiState.showSaveDialog,
        showSummaryDialog = uiState.showSummaryDialog,
        totalHits = uiState.totalHits,
        totalShots = uiState.totalShots,
        onDismissEndSession = onDismissEndSession,
        onConfirmEndSession = onConfirmEndSession,
        onDismissSaveDialog = onDismissSaveDialog,
        onConfirmSave = onConfirmSave,
        onConfirmErase = onConfirmErase,
        onDismissSummary = onDismissSummary
    )

    Scaffold(
        topBar = {
            Column {
                SessionEndTitleBar(
                    sportName = "Free Shoot",
                    onStopSession = onStopSession
                )
                // Header section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(primaryBlue)
                        .padding(bottom = 16.dp, top = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "FREE SHOOT",
                        color = Color.White,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Informational Stats section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatRow(label = "Shots Taken:", value = "${uiState.totalShots}", primaryBlue)
                StatRow(label = "Hits:", value = "${uiState.totalHits}", primaryBlue)
                StatRow(label = "Misses:", value = "${uiState.totalMisses}", primaryBlue)
                StatRow(label = "Accuracy:", value = "${String.format("%.1f", uiState.accuracy)}%", primaryBlue)
            }

            Spacer(modifier = Modifier.weight(1f))

            // Hit/Miss Buttons
            HitMissButtons(
                onHit = onHit,
                onMiss = onMiss
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun StatRow(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = value,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = color
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NewFreeShootScreenPreview() {
    UofACapstoneTheme {
        NewFreeShootContent(
            uiState = FreeShootUiState(totalShots = 10, totalHits = 7),
            onHit = {},
            onMiss = {},
            onStopSession = {},
            onDismissEndSession = {},
            onConfirmEndSession = {},
            onDismissSaveDialog = {},
            onConfirmSave = {},
            onConfirmErase = {},
            onDismissSummary = {}
        )
    }
}
