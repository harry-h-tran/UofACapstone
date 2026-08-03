package com.example.uofacapstone.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.uofacapstone.ui.components.BottomToolbar
import com.example.uofacapstone.ui.components.SessionDialogs
import com.example.uofacapstone.ui.theme.UofACapstoneTheme

import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.uofacapstone.ui.viewmodels.MainViewModel
import com.example.uofacapstone.ui.viewmodels.NewSessionDetailsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewSessionDetailsScreen(
    sport: String,
    onStartSession: (String, Long) -> Unit,
    onBack: () -> Unit,
    onStatsClick: () -> Unit,
    onSessionsClick: () -> Unit,
    onAddClick: () -> Unit,
    viewModel: NewSessionDetailsViewModel,
    mainViewModel: MainViewModel
) {
    val latestSession by mainViewModel.inProgressSession.collectAsState()

    SessionDialogs(
        mainViewModel = mainViewModel,
        onContinueClick = onAddClick
    )

    NewSessionDetailsContent(
        sport = sport,
        location = viewModel.location,
        onLocationChange = viewModel::updateLocation,
        notes = viewModel.notes,
        onNotesChange = viewModel::updateNotes,
        freeShootOptions = viewModel.freeShootOptions,
        selectedFreeShootType = viewModel.selectedFreeShootType,
        onFreeShootTypeChange = viewModel::updateFreeShootType,
        onStartSession = {
            viewModel.startSession(sport) { sessionId ->
                onStartSession(sport, sessionId)
            }
        },
        onBack = onBack,
        onStatsClick = onStatsClick,
        onSessionsClick = onSessionsClick,
        onAddClick = { mainViewModel.handleAddButtonClick(onAddClick) },
        isSessionInProgress = latestSession != null
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewSessionDetailsContent(
    sport: String,
    location: String,
    onLocationChange: (String) -> Unit,
    notes: String,
    onNotesChange: (String) -> Unit,
    freeShootOptions: List<String>,
    selectedFreeShootType: String,
    onFreeShootTypeChange: (String) -> Unit,
    onStartSession: () -> Unit,
    onBack: () -> Unit,
    onStatsClick: () -> Unit,
    onSessionsClick: () -> Unit,
    onAddClick: () -> Unit,
    isSessionInProgress: Boolean = false
) {
    BackHandler(onBack = onBack)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Session Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
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
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "New $sport Session:",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = location,
                onValueChange = onLocationChange,
                label = { Text("Location") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            if (sport.lowercase() == "free shoot") {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Type:",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
                freeShootOptions.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onFreeShootTypeChange(option) }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (selectedFreeShootType == option),
                            onClick = { onFreeShootTypeChange(option) }
                        )
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = onNotesChange,
                label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onStartSession,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Start Session")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NewSessionDetailsSkeetPreview() {
    UofACapstoneTheme {
        NewSessionDetailsContent(
            sport = "Skeet",
            location = "",
            onLocationChange = {},
            notes = "",
            onNotesChange = {},
            freeShootOptions = listOf("Skeet", "Trap", "Sporting Clays", "No Sport/Free Form"),
            selectedFreeShootType = "No Sport/Free Form",
            onFreeShootTypeChange = {},
            onStartSession = {},
            onBack = {},
            onStatsClick = {},
            onSessionsClick = {},
            onAddClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NewSessionDetailsFreeShootPreview() {
    UofACapstoneTheme {
        NewSessionDetailsContent(
            sport = "Free Shoot",
            location = "",
            onLocationChange = {},
            notes = "",
            onNotesChange = {},
            freeShootOptions = listOf("Skeet", "Trap", "Sporting Clays", "No Sport/Free Form"),
            selectedFreeShootType = "No Sport/Free Form",
            onFreeShootTypeChange = {},
            onStartSession = {},
            onBack = {},
            onStatsClick = {},
            onSessionsClick = {},
            onAddClick = {}
        )
    }
}
