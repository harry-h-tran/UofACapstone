package com.harrytran.claytracker.ui.screens

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
import com.harrytran.claytracker.ui.theme.UofACapstoneTheme
import com.harrytran.claytracker.ui.viewmodels.SportingClaysViewModel
import com.harrytran.claytracker.ui.viewmodels.SportingClaysUiState
import com.harrytran.claytracker.ui.components.EndSessionDialog
import com.harrytran.claytracker.ui.components.HitMissButtons
import com.harrytran.claytracker.ui.components.SessionEndTitleBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewSportingClaysSessionScreen(
    viewModel: SportingClaysViewModel = viewModel(),
    onBack: () -> Unit,
    onSessionFinished: (Long) -> Unit
) {
    BackHandler(onBack = onBack)
    val uiState by viewModel.uiState.collectAsState()

    NewSportingClaysSessionContent(
        uiState = uiState,
        onUpdateStation = { viewModel.updateStation(it) },
        onUpdateTarget = { viewModel.updateTarget(it) },
        onUpdateShotType = { viewModel.updateShotType(it) },
        onUpdatePairType = { viewModel.updatePairType(it) },
        onHit = { viewModel.onHit() },
        onMiss = { viewModel.onMiss() },
        onStopSession = { viewModel.onStopSession() },
        onDismissEndSession = { viewModel.completeSession(false) },
        onConfirmEndSession = { viewModel.completeSession(true) },
        onDismissSaveDialog = { viewModel.dismissSaveDialog() },
        onConfirmSave = { viewModel.saveSession() },
        onConfirmErase = { viewModel.eraseSession(onBack) },
        onDismissSummary = { viewModel.dismissSummaryDialog(onSessionFinished) },
        onCancelEndSession = { viewModel.dismissEndSessionDialog() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewSportingClaysSessionContent(
    uiState: SportingClaysUiState,
    onUpdateStation: (Int) -> Unit,
    onUpdateTarget: (Int) -> Unit,
    onUpdateShotType: (String) -> Unit,
    onUpdatePairType: (String) -> Unit,
    onHit: () -> Unit,
    onMiss: () -> Unit,
    onStopSession: () -> Unit,
    onDismissEndSession: () -> Unit,
    onConfirmEndSession: () -> Unit,
    onDismissSaveDialog: () -> Unit,
    onConfirmSave: () -> Unit,
    onConfirmErase: () -> Unit,
    onDismissSummary: () -> Unit,
    onCancelEndSession: () -> Unit
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
        onDismissSummary = onDismissSummary,
        endSessionTitle = "Session Complete?",
        endSessionText = "Was this session the full course?",
        confirmButtonText = "Yes - recorded as Complete Session",
        dismissButtonText = "No - Recorded as Incomplete Session",
        onCancelEndSession = onCancelEndSession
    )

    Scaffold(
        topBar = {
            Column {
                SessionEndTitleBar(
                    sportName = "Sporting Clays",
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
                        text = "SPORTING CLAYS",
                        color = Color.White,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Station Selection
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { if (uiState.station > 1) onUpdateStation(uiState.station - 1) }) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Decrease Station",
                        modifier = Modifier.size(32.dp),
                        tint = primaryBlue
                    )
                }
                Text(
                    text = "Station: ${uiState.station}",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryBlue
                )
                IconButton(onClick = { if (uiState.station < 15) onUpdateStation(uiState.station + 1) }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increase Station",
                        modifier = Modifier.size(32.dp),
                        tint = primaryBlue
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp),
                thickness = 1.dp,
                color = Color.LightGray.copy(alpha = 0.5f)
            )

            // Target Selection
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { if (uiState.target > 1) onUpdateTarget(uiState.target - 1) }) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Decrease Target",
                        modifier = Modifier.size(32.dp),
                        tint = primaryBlue
                    )
                }
                Text(
                    text = "Target: ${uiState.target}",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryBlue
                )
                IconButton(onClick = { if (uiState.target < 10) onUpdateTarget(uiState.target + 1) }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increase Target",
                        modifier = Modifier.size(32.dp),
                        tint = primaryBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Shot Type Selection (Single/Double)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = uiState.shotType == "Single",
                        onClick = { onUpdateShotType("Single") }
                    )
                    Text("Single", fontSize = 20.sp, fontWeight = FontWeight.Medium)
                }
                Spacer(modifier = Modifier.width(32.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = uiState.shotType == "Double",
                        onClick = { onUpdateShotType("Double") }
                    )
                    Text("Double", fontSize = 20.sp, fontWeight = FontWeight.Medium)
                }
            }

            // Double Options (Visible only when Double is selected)
            if (uiState.shotType == "Double") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        RadioButton(
                            selected = uiState.pairType == "True Pair",
                            onClick = { onUpdatePairType("True Pair") }
                        )
                        Text("True Pair", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(16.dp))
                        RadioButton(
                            selected = uiState.pairType == "Reporting Pair",
                            onClick = { onUpdatePairType("Reporting Pair") }
                        )
                        Text("Reporting Pair", fontSize = 16.sp)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        RadioButton(
                            selected = uiState.pairType == "Following Pair",
                            onClick = { onUpdatePairType("Following Pair") }
                        )
                        Text("Following Pair", fontSize = 16.sp)
                    }
                }
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

@Preview(showBackground = true)
@Composable
fun NewSportingClaysSessionScreenPreview() {
    UofACapstoneTheme {
        NewSportingClaysSessionContent(
            uiState = SportingClaysUiState(),
            onUpdateStation = {},
            onUpdateTarget = {},
            onUpdateShotType = {},
            onUpdatePairType = {},
            onHit = {},
            onMiss = {},
            onStopSession = {},
            onDismissEndSession = {},
            onConfirmEndSession = {},
            onDismissSaveDialog = {},
            onConfirmSave = {},
            onConfirmErase = {},
            onDismissSummary = {},
            onCancelEndSession = {}
        )
    }
}
