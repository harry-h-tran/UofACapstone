package com.example.uofacapstone.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.uofacapstone.ui.viewmodels.TrapViewModel
import com.example.uofacapstone.ui.viewmodels.TrapUiState
import com.example.uofacapstone.ui.theme.UofACapstoneTheme
import com.example.uofacapstone.ui.components.EndSessionDialog
import com.example.uofacapstone.ui.components.HitMissButtons
import com.example.uofacapstone.ui.components.SessionEndTitleBar
import kotlin.math.cos
import kotlin.math.sin

import com.example.uofacapstone.ui.components.TrackingButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTrapSessionScreen(
    viewModel: TrapViewModel = viewModel(),
    onBack: () -> Unit,
    onSessionFinished: (Long) -> Unit
) {
    BackHandler(onBack = onBack)
    val uiState by viewModel.uiState.collectAsState()
    NewTrapSessionContent(
        uiState = uiState,
        onHit = { viewModel.onHit() },
        onMiss = { viewModel.onMiss() },
        onUpdateStation = { viewModel.updateStation(it) },
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
fun NewTrapSessionContent(
    uiState: TrapUiState,
    onHit: () -> Unit,
    onMiss: () -> Unit,
    onUpdateStation: (Int) -> Unit,
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
                    sportName = "Trap",
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
                        text = "TRAP SESSION",
                        color = Color.White,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "STATION: ${uiState.currentStation}",
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

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

            // Hit/Miss Buttons or Finish flow
            if (!uiState.isSessionComplete) {
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
        val width = this.maxWidth
        val padding = 15.dp

        val cX = width / 2
        val cY = 0.dp
        val radiusX = (width - padding * 2) / 1.5f
        val radiusY =radiusX * .75f

        // Arc
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = cX.toPx()
            val centerY = cY.toPx()
            val rx = radiusX.toPx()
            val ry = radiusY.toPx()

            drawArc(
                color = Color.Gray,
                startAngle = 30f,
                sweepAngle = 120f,
                useCenter = false,
                topLeft = androidx.compose.ui.geometry.Offset(centerX - rx, centerY - ry),
                size = androidx.compose.ui.geometry.Size(rx * 2f, ry * 2f),
                style = Stroke(width = 10.dp.toPx())
            )

        }

        for (i in 1..5) {
            val x: androidx.compose.ui.unit.Dp
            val y: androidx.compose.ui.unit.Dp
            val angleRad: Double

            val angle = 150f - (i - 1) * 30f
            angleRad = Math.toRadians(angle.toDouble())
            x = cX + radiusX * cos(angleRad).toFloat()
            y = cY + radiusY * sin(angleRad).toFloat()

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
                var offsetX = 25.dp
                var offsetY = 30.dp

                lx = x
                ly = y

                Text(
                    text = i.toString(),
                    modifier = Modifier
                        .offset(lx - offsetX, ly + offsetY)
                        .width(50.dp),
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
fun NewTrapSessionScreenPreview() {
    UofACapstoneTheme {
        NewTrapSessionContent(
            uiState = TrapUiState(currentStation = 1, totalHits = 5, totalShots = 7),
            onHit = {},
            onMiss = {},
            onUpdateStation = {},
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
