package com.example.uofacapstone.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.uofacapstone.ui.components.BottomToolbar
import com.example.uofacapstone.ui.components.SessionDialogs
import com.example.uofacapstone.ui.viewmodels.MainViewModel
import com.example.uofacapstone.ui.viewmodels.NewSessionViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewSessionScreen(
    onSportSelected: (String) -> Unit,
    onBack: () -> Unit,
    onStatsClick: () -> Unit,
    onSessionsClick: () -> Unit,
    onAddClick: () -> Unit,
    viewModel: NewSessionViewModel = viewModel(),
    mainViewModel: MainViewModel
) {
    val latestSession by mainViewModel.inProgressSession.collectAsState()

    SessionDialogs(
        mainViewModel = mainViewModel,
        onContinueClick = onAddClick
    )

    NewSessionContent(
        sportsOptions = viewModel.sportsOptions,
        onSportSelected = onSportSelected,
        onBack = onBack,
        onStatsClick = onStatsClick,
        onSessionsClick = onSessionsClick,
        onAddClick = { mainViewModel.handleAddButtonClick(onAddClick) },
        isSessionInProgress = latestSession != null
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewSessionContent(
    sportsOptions: List<String>,
    onSportSelected: (String) -> Unit,
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
                title = { Text("Select Sport") },
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
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(sportsOptions) { option ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clickable { onSportSelected(option) },
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = option,
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}
