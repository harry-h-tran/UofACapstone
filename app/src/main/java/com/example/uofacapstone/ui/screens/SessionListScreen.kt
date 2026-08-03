package com.example.uofacapstone.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uofacapstone.data.Session
import com.example.uofacapstone.data.SessionStatus
import com.example.uofacapstone.ui.components.BottomToolbar
import com.example.uofacapstone.ui.components.SessionDialogs
import com.example.uofacapstone.ui.theme.UofACapstoneTheme
import com.example.uofacapstone.ui.viewmodels.MainViewModel
import com.example.uofacapstone.ui.viewmodels.SessionListUiState
import com.example.uofacapstone.ui.viewmodels.SessionListViewModel
import androidx.compose.ui.tooling.preview.Preview
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SessionListScreen(
    onStatsClick: () -> Unit,
    onAddClick: () -> Unit,
    onSessionsClick: () -> Unit,
    onSessionClick: (Long) -> Unit,
    viewModel: SessionListViewModel,
    mainViewModel: MainViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val latestSession by mainViewModel.inProgressSession.collectAsState()

    SessionDialogs(
        mainViewModel = mainViewModel,
        onContinueClick = onAddClick
    )

    SessionListContent(
        uiState = uiState,
        isSessionInProgress = latestSession != null,
        onStatsClick = onStatsClick,
        onAddClick = { mainViewModel.handleAddButtonClick(onAddClick) },
        onSessionsClick = onSessionsClick,
        onSessionClick = onSessionClick
    )
}

@Composable
fun SessionListContent(
    uiState: SessionListUiState,
    isSessionInProgress: Boolean,
    onStatsClick: () -> Unit,
    onAddClick: () -> Unit,
    onSessionsClick: () -> Unit,
    onSessionClick: (Long) -> Unit
) {
    Scaffold(
        bottomBar = {
            BottomToolbar(
                onStatsClick = onStatsClick,
                onAddClick = onAddClick,
                onSessionsClick = onSessionsClick,
                isSessionInProgress = isSessionInProgress
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "All Sessions",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.sessions) { session ->
                    SessionRow(session = session, onClick = { onSessionClick(session.id) })
                }
            }
        }
    }
}


@Composable
fun SessionRow(session: Session, onClick: () -> Unit) {
    val isIncomplete = session.status == SessionStatus.INCOMPLETE || session.status == SessionStatus.IN_PROGRESS
    val backgroundColor = if (isIncomplete) Color(0xFFFFEBEE) else MaterialTheme.colorScheme.surfaceVariant

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
            contentColor = Color.Black
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = session.sport,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = formatDate(session.dateTime),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = session.location,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isIncomplete) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Incomplete",
                        tint = Color.Red,
                        modifier = Modifier.size(20.dp).padding(end = 4.dp)
                    )
                }
                
                val scoreText = if (session.score != null) "${session.score}" else "Inc. Session"
                val accuracyText = if (session.accuracy != null) {
                    " (${String.format(Locale.getDefault(), "%.0f", session.accuracy * 100)}%)"
                } else ""
                
                Text(
                    text = scoreText + accuracyText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = if (isIncomplete) Color.Red else Color.Unspecified
                )
            }
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy - h:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

@Preview(showBackground = true)
@Composable
fun SessionListPreview() {
    UofACapstoneTheme {
        val mockSessions = listOf(
            Session(id = 1, dateTime = System.currentTimeMillis(), location = "Club A", sport = "Skeet", score = 23, accuracy = 0.92, status = SessionStatus.COMPLETED),
            Session(id = 2, dateTime = System.currentTimeMillis() - 86400000, location = "Club B", sport = "Trap", score = 20, accuracy = 0.8, status = SessionStatus.COMPLETED),
            Session(id = 3, dateTime = System.currentTimeMillis() - 172800000, location = "Club C", sport = "Sporting Clays", status = SessionStatus.INCOMPLETE)
        )
        SessionListContent(
            uiState = SessionListUiState(mockSessions),
            isSessionInProgress = false,
            onStatsClick = {},
            onAddClick = {},
            onSessionsClick = {},
            onSessionClick = {}
        )
    }
}

