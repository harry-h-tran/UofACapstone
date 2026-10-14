package com.harrytran.claytracker.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harrytran.claytracker.ui.theme.UofACapstoneTheme
import kotlin.math.cos
import kotlin.math.sin
import androidx.lifecycle.viewmodel.compose.viewModel
import com.harrytran.claytracker.ui.viewmodels.SkeetViewModel
import com.harrytran.claytracker.ui.viewmodels.SkeetUiState
import com.harrytran.claytracker.ui.components.EndSessionDialog
import com.harrytran.claytracker.ui.components.HitMissButtons
import com.harrytran.claytracker.ui.components.SessionEndTitleBar
import com.harrytran.claytracker.ui.components.TrackingButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewSkeetSessionScreen(
    viewModel: SkeetViewModel = viewModel(),
    onBack: () -> Unit,
    onSessionFinished: (Long) -> Unit
) {
    BackHandler(onBack = onBack)
    val uiState by viewModel.uiState.collectAsState()
    
    NewSkeetSessionContent(
        uiState = uiState,
        onHit = { viewModel.onHit() },
        onMiss = { viewModel.onMiss() },
        onBack = onBack,
        onDismissOption = { viewModel.dismissOptionDialog() },
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
fun NewSkeetSessionContent(
    uiState: SkeetUiState,
    onHit: () -> Unit,
    onMiss: () -> Unit,
    onBack: () -> Unit,
    onDismissOption: () -> Unit,
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

    if (uiState.showOptionDialog) {
        AlertDialog(
            onDismissRequest = { }, // Force user to click button
            title = { Text(uiState.optionDialogTitle) },
            text = if (uiState.optionDialogMessage.isNotEmpty()) {
                { Text(uiState.optionDialogMessage) }
            } else null,
            confirmButton = {
                Button(onClick = onDismissOption) {
                    Text("Option")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            Column {
                SessionEndTitleBar(
                    sportName = "Skeet",
                    onStopSession = onStopSession
                )
                
                // Header section with station and shot info
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(primaryBlue)
                        .padding(bottom = 16.dp, top = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "STATION: ${uiState.currentStation}",
                        color = Color.White,
                        fontSize = 56.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Target: ${uiState.currentTargetLabel}",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "TOTAL: ${uiState.totalHits} / ${uiState.totalShots}",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Station Visualization
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .padding(horizontal = 40.dp)
            ) {
                StationArc(currentStation = uiState.currentStation, primaryColor = primaryBlue)
            }

            Spacer(modifier = Modifier.weight(1f))

            // Hit/Miss Buttons
            if (!uiState.isFinished) {
                HitMissButtons(
                    onHit = onHit,
                    onMiss = onMiss
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TrackingButton(
                        text = "DONE",
                        onClick = { },
                        enabled = false,
                        containerColor = Color(0xFF4CAF50)
                    )
                    TrackingButton(
                        text = "FINISH",
                        onClick = onStopSession,
                        containerColor = primaryBlue,
                        fontSize = 60.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun StationArc(currentStation: Int, primaryColor: Color) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val width = this.maxWidth - 15.dp
        val padding = 15.dp
        
        val cX = width / 2
        val cY = 60.dp
        val radius = (width - padding * 2) / 2

        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = cX.toPx()
            val centerY = cY.toPx()
            val r = radius.toPx()

            drawArc(
                color = Color.Gray,
                startAngle = 0f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = androidx.compose.ui.geometry.Offset(centerX - r, centerY - r),
                size = androidx.compose.ui.geometry.Size(r * 2, r * 2),
                style = Stroke(width = 5.dp.toPx())
            )
        }

        for (i in 1..8) {
            val x: androidx.compose.ui.unit.Dp
            val y: androidx.compose.ui.unit.Dp
            val angleRad: Double
            
            if (i <= 7) {
                val angle = 180f - (i - 1) * (180f / 6f)
                angleRad = Math.toRadians(angle.toDouble())
                x = cX + radius * cos(angleRad).toFloat()
                y = cY + radius * sin(angleRad).toFloat()
            } else {
                angleRad = 0.0
                x = cX
                y = cY
            }

            if (i == currentStation) {
                Surface(
                    modifier = Modifier
                        .offset(x - 30.dp, y - 30.dp)
                        .size(60.dp),
                    shape = CircleShape,
                    color = primaryColor
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = i.toString(),
                            color = Color.White,
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .offset(x - 10.dp, y - 10.dp)
                        .size(20.dp)
                        .background(primaryColor, CircleShape)
                )
                
                val lx: androidx.compose.ui.unit.Dp
                val ly: androidx.compose.ui.unit.Dp
                var offsetX = 50.dp
                var offsetY = 50.dp

                if (i <= 7) {
                    val labelRadius = radius + 60.dp
                    lx = cX + labelRadius * cos(angleRad).toFloat()
                    ly = cY + labelRadius * sin(angleRad).toFloat()
                    when (i) {
                        1 -> { offsetX = 30.dp; offsetY = 30.dp }
                        7 -> { offsetX = 70.dp; offsetY = 30.dp }
                        2 -> { offsetX = 40.dp; offsetY = 50.dp }
                        6 -> { offsetX = 60.dp; offsetY = 50.dp }
                    }
                } else {
                    lx = cX
                    ly = cY + 60.dp
                }
                
                Text(
                    text = i.toString(),
                    modifier = Modifier
                        .offset(lx - offsetX, ly - offsetY)
                        .width(100.dp),
                    fontSize = 40.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NewSkeetSessionScreenPreview() {
    UofACapstoneTheme {
        NewSkeetSessionContent(
            uiState = SkeetUiState(
                currentStation = 1,
                currentTargetLabel = "High",
                totalHits = 10,
                totalShots = 15
            ),
            onHit = {},
            onMiss = {},
            onBack = {},
            onDismissOption = {},
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
