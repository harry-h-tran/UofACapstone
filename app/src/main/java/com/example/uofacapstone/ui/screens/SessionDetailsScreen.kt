package com.example.uofacapstone.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
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
import com.example.uofacapstone.data.ShotEntry
import com.example.uofacapstone.ui.viewmodels.SessionDetailsViewModel
import com.example.uofacapstone.ui.viewmodels.SessionDetailsUiState
import com.example.uofacapstone.data.SessionStatus
import com.example.uofacapstone.ui.theme.UofACapstoneTheme
import androidx.compose.ui.tooling.preview.Preview
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionDetailsScreen(
    viewModel: SessionDetailsViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    SessionDetailsContent(uiState, onBack)
}

@Composable
fun SessionHeader(session: Session) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = session.sport, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
            Text(text = formatDate(session.dateTime), style = MaterialTheme.typography.bodyLarge)
            Text(text = "Location: ${session.location}", style = MaterialTheme.typography.bodyMedium)
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Score", style = MaterialTheme.typography.labelMedium)
                    Text(
                        text = if (session.score != null) "${session.score}" else "Incomplete Session",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Accuracy", style = MaterialTheme.typography.labelMedium)
                    Text(
                        text = if (session.accuracy != null) "${String.format(Locale.getDefault(), "%.1f", session.accuracy * 100)}%" else "N/A",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            if (session.notes?.isNotEmpty() == true) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Notes", style = MaterialTheme.typography.labelMedium)
                Text(text = session.notes, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun ShotList(shots: List<ShotEntry>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(shots) { shot ->
            ShotRow(shot)
        }
    }
}

@Composable
fun ShotRow(shot: ShotEntry) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "Station ${shot.station}${if (shot.target != null) " - ${shot.target}" else ""}",
                fontWeight = FontWeight.Medium
            )
            if (shot.sublabel != null) {
                Text(text = shot.sublabel, style = MaterialTheme.typography.bodySmall)
            }
        }
        
        Icon(
            imageVector = if (shot.isHit) Icons.Default.CheckCircle else Icons.Default.Cancel,
            contentDescription = if (shot.isHit) "Hit" else "Miss",
            tint = if (shot.isHit) Color(0xFF4CAF50) else Color.Red
        )
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("EEEE, MMMM dd, yyyy - h:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

@Preview(showBackground = true)
@Composable
fun SessionDetailsPreview() {
    UofACapstoneTheme {
        val mockSession = Session(
            id = 1L,
            dateTime = System.currentTimeMillis(),
            location = "Evergreen Shooting Club",
            sport = "Skeet",
            score = 22,
            accuracy = 0.88,
            notes = "Feeling good today. Wind was minimal.",
            status = SessionStatus.COMPLETED
        )
        val mockShots = listOf(
            ShotEntry(shotId = 1, sessionId = 1, station = 1, target = "High House", sublabel = null, isHit = true),
            ShotEntry(shotId = 2, sessionId = 1, station = 1, target = "Low House", sublabel = null, isHit = false),
            ShotEntry(shotId = 3, sessionId = 1, station = 2, target = "High House", sublabel = null, isHit = true),
            ShotEntry(shotId = 4, sessionId = 1, station = 2, target = "Low House", sublabel = null, isHit = true)
        )

        SessionDetailsContent(
            uiState = SessionDetailsUiState(
                session = mockSession,
                shots = mockShots,
                isLoading = false
            ),
            onBack = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionDetailsContent(
    uiState: SessionDetailsUiState,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Session Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            val session = uiState.session
            if (session != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp)
                ) {
                    SessionHeader(session)
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Shots",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    if (uiState.shots.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxWidth().weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No shots recorded for this session.", color = Color.Gray)
                        }
                    } else {
                        ShotList(uiState.shots, Modifier.weight(1f))
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Session not found")
                }
            }
        }
    }
}

