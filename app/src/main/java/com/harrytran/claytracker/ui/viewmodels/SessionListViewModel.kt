package com.harrytran.claytracker.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harrytran.claytracker.data.Session
import com.harrytran.claytracker.data.SessionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class SessionListUiState(
    val sessions: List<Session> = emptyList()
)

class SessionListViewModel(
    private val repository: SessionRepository
) : ViewModel() {

    val uiState: StateFlow<SessionListUiState> = repository.allSessions
        .map { SessionListUiState(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SessionListUiState()
        )
}
