package com.harrytran.claytracker.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.harrytran.claytracker.data.Session
import com.harrytran.claytracker.data.SessionRepository
import com.harrytran.claytracker.data.ShotEntry
import kotlinx.coroutines.flow.*

data class SessionDetailsUiState(
    val session: Session? = null,
    val shots: List<ShotEntry> = emptyList(),
    val isLoading: Boolean = true
)

class SessionDetailsViewModel(
    private val repository: SessionRepository,
    private val sessionId: Long
) : ViewModel() {

    val uiState: StateFlow<SessionDetailsUiState> = combine(
        repository.getSessionByIdFlow(sessionId),
        repository.getShotsForSession(sessionId)
    ) { session, shots ->
        SessionDetailsUiState(
            session = session,
            shots = shots,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SessionDetailsUiState(isLoading = true)
    )
}

class SessionDetailsViewModelFactory(
    private val repository: SessionRepository,
    private val sessionId: Long
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SessionDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SessionDetailsViewModel(repository, sessionId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
