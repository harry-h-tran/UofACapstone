package com.harrytran.claytracker.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harrytran.claytracker.data.Session
import com.harrytran.claytracker.data.SessionStatus
import com.harrytran.claytracker.ui.components.BottomToolbar
import com.harrytran.claytracker.ui.components.SessionDialogs
import com.harrytran.claytracker.ui.theme.UofACapstoneTheme
import com.harrytran.claytracker.ui.viewmodels.MainViewModel
import com.harrytran.claytracker.ui.viewmodels.StatsUiState
import com.harrytran.claytracker.ui.viewmodels.StatsViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun StatsScreen(
    onStatsClick: () -> Unit,
    onAddClick: () -> Unit,
    onSessionsClick: () -> Unit,
    viewModel: StatsViewModel,
    mainViewModel: MainViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val latestSession by mainViewModel.inProgressSession.collectAsState()

    SessionDialogs(
        mainViewModel = mainViewModel,
        onContinueClick = onAddClick
    )

    StatsScreenContent(
        onStatsClick = onStatsClick,
        onSessionsClick = onSessionsClick,
        uiState = uiState,
        onAddClick = { mainViewModel.handleAddButtonClick(onAddClick) },
        isSessionInProgress = latestSession != null
    )
}

@Composable
fun StatsScreenContent(
    uiState: StatsUiState,
    onStatsClick: () -> Unit,
    onSessionsClick: () -> Unit,
    onAddClick: () -> Unit,
    isSessionInProgress: Boolean = false
) {
    Scaffold(
        bottomBar = {
            BottomToolbar(
                onStatsClick = onStatsClick,
                onSessionsClick = onSessionsClick,
                onAddClick = onAddClick,
                isSessionInProgress = isSessionInProgress
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // ... (rest of the file remains the same)
            Text(
                text = "Accuracy Performance",
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                text = "(Completed Sessions Only)",
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Summary Metrics Tiled Section
            SummaryMetricsSection(uiState.averageSession, uiState.bestSession, uiState.worstSession)

            Spacer(modifier = Modifier.height(24.dp))

            // Line Graphs for each sport
            Text(
                text = "Sport Accuracy",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            AccuracyGraph(title = "Skeet", sessions = uiState.skeetSessions)
            Spacer(modifier = Modifier.height(16.dp))

            AccuracyGraph(title = "Trap", sessions = uiState.trapSessions)
            Spacer(modifier = Modifier.height(16.dp))

            AccuracyGraph(title = "Sporting Clays", sessions = uiState.sportingClaysSessions)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun SummaryMetricsSection(average: Session?, best: Session?, worst: Session?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SummaryTile("Average", average, Modifier.weight(1f), Color(0xFFBBDEFB)) // Light blue
        SummaryTile("Best", best, Modifier.weight(1f), Color(0xFFC8E6C9)) // Light green
        SummaryTile("Worst", worst, Modifier.weight(1f), Color(0xFFFFCDD2)) // Light red
    }
}

@Composable
fun SummaryTile(label: String, session: Session?, modifier: Modifier = Modifier, backgroundColor: Color) {
    Card(
        modifier = modifier.height(140.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
            contentColor = Color.Black
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, fontWeight = FontWeight.Bold, fontSize = 25.sp, color = Color.Black)
            
            if (session != null) {
                Column {
                    Text(
                        text = if (label == "Average") "All Sports" else session.sport,
                        fontSize = 15.sp
                    )
                    Text(
                        text = if (label == "Average") "All Sessions" else formatDate(session.dateTime),
                        fontSize = 15.sp,
                        color = Color.Black
                    )
                    Text(
                        text = "${String.format(Locale.getDefault(), "%.1f", (session.accuracy ?: 0.0) * 100)}%",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            } else {
                Text(
                    text = "No Data", 
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun AccuracyGraph(title: String, sessions: List<Session>) {
    val sortedSessions = remember(sessions) {
        sessions.sortedBy { it.dateTime }
    }

    val textMeasurer = rememberTextMeasurer()
    val labelStyle = MaterialTheme.typography.labelSmall.copy(
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Text(text = title, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .padding(start = 45.dp, end = 20.dp, bottom = 35.dp),
            contentAlignment = Alignment.Center
        ) {
            if (sortedSessions.isEmpty()) {
                Text(
                    text = "No $title Data",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }

            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height

                // Draw Y-axis labels and grid lines (0% to 100%)
                val yLabels = listOf("100%", "75%", "50%", "25%", "0%")
                yLabels.forEachIndexed { index, label ->
                    val y = index * (height / (yLabels.size - 1))

                    // Grid line
                    drawLine(
                        color = Color.Gray.copy(alpha = 0.2f),
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = 1.dp.toPx()
                    )

                    // Label
                    val textLayoutResult = textMeasurer.measure(label, labelStyle)
                    drawText(
                        textLayoutResult = textLayoutResult,
                        topLeft = Offset(
                            x = -textLayoutResult.size.width.toFloat() - 8.dp.toPx(),
                            y = y - textLayoutResult.size.height / 2f
                        )
                    )
                }

                // Draw Axis Lines
                drawLine(
                    color = Color.Gray,
                    start = Offset(0f, 0f),
                    end = Offset(0f, height),
                    strokeWidth = 2.dp.toPx()
                )
                drawLine(
                    color = Color.Gray,
                    start = Offset(0f, height),
                    end = Offset(width, height),
                    strokeWidth = 2.dp.toPx()
                )

                if (sortedSessions.isNotEmpty()) {
                    val path = Path()
                    // If only one session, place it at x=0 (intercepting y-axis)
                    val stepX = if (sortedSessions.size > 1) width / (sortedSessions.size - 1) else 0f

                    sortedSessions.forEachIndexed { index, session ->
                        val x = index * stepX
                        val y = height * (1f - (session.accuracy?.toFloat() ?: 0f))

                        if (index == 0) {
                            path.moveTo(x, y)
                        } else {
                            path.lineTo(x, y)
                        }

                        drawCircle(
                            color = Color.Blue,
                            radius = 4.dp.toPx(),
                            center = Offset(x, y)
                        )

                        // Draw X-axis date label angled
                        val dateStr = formatShortDate(session.dateTime)
                        val textLayoutResult = textMeasurer.measure(dateStr, labelStyle)
                        val tHeight = textLayoutResult.size.height.toFloat()

                        val labelYOffset = height + 30.dp.toPx()

                        rotate(degrees = -45f, pivot = Offset(x, labelYOffset)) {
                            drawText(
                                textLayoutResult = textLayoutResult,
                                topLeft = Offset(x, labelYOffset - tHeight / 2f)
                            )
                        }
                    }

                    if (sortedSessions.size > 1) {
                        drawPath(
                            path = path,
                            color = Color.Blue,
                            style = Stroke(width = 2.dp.toPx())
                        )
                    }
                }
            }
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MM/dd/yy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

private fun formatShortDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MM/dd", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

@Preview(showBackground = true)
@Composable
fun StatsScreenPreview() {
    UofACapstoneTheme {
        // Mocking sessions for preview
        val now = System.currentTimeMillis()
        val day = 24 * 60 * 60 * 1000L
        
        val skeetSessions = listOf(
            Session(1, now - 5 * day, "Club A", "Skeet", score = 20, accuracy = 0.8, status = SessionStatus.COMPLETED),
            Session(2, now - 3 * day, "Club A", "Skeet", score = 22, accuracy = 0.88, status = SessionStatus.COMPLETED),
            Session(3, now - 1 * day, "Club B", "Skeet", score = 18, accuracy = 0.72, status = SessionStatus.COMPLETED)
        )
        
        val trapSessions = listOf(
            Session(4, now - 4 * day, "Club C", "Trap", score = 24, accuracy = 0.96, status = SessionStatus.COMPLETED),
            Session(5, now - 2 * day, "Club C", "Trap", score = 25, accuracy = 1.0, status = SessionStatus.COMPLETED)
        )

        val best = trapSessions[1]
        val worst = skeetSessions[2]
        val average = Session(0, now, "Aggregate", "All", score = 21, accuracy = 0.84, status = SessionStatus.COMPLETED)

        val uiState = StatsUiState(
            averageSession = average,
            bestSession = best,
            worstSession = worst,
            skeetSessions = skeetSessions,
            trapSessions = trapSessions
        )

        StatsScreenContent(
            uiState = uiState,
            onStatsClick = {},
            onSessionsClick = {},
            onAddClick = {}
        )
    }
}
