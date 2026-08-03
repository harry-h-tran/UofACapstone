package com.example.uofacapstone.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uofacapstone.data.SessionRepository
import com.example.uofacapstone.data.ShotEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Base ViewModel for sports sessions that handles the common "End Session" flow.
 *
 * @param S The type of the UI state, which should contain session-related flags.
 * @param repository The repository for session data operations.
 * @param sessionId The ID of the current session.
 * @param initialState The initial state for the session.
 */
abstract class BaseSessionViewModel<S>(
    protected val repository: SessionRepository,
    protected val sessionId: Long,
    initialState: S
) : ViewModel() {
    protected val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<S> = _uiState.asStateFlow()

    init {
        loadExistingShots()
    }

    private fun loadExistingShots() {
        viewModelScope.launch {
            repository.getShotsForSession(sessionId).collect { shots ->
                _uiState.update { restoreStateFromShots(it, shots) }
            }
        }
    }

    /**
     * Subclasses must implement this to restore their specific state from the list of shots.
     */
    protected abstract fun restoreStateFromShots(state: S, shots: List<ShotEntry>): S

    /**
     * Subclasses must implement this to return a copy of the state with the end session dialog visibility updated.
     */
    protected abstract fun updateEndSessionDialog(state: S, show: Boolean): S

    /**
     * Subclasses must implement this to return a copy of the state with the save dialog visibility updated.
     */
    protected abstract fun updateSaveDialog(state: S, show: Boolean): S

    /**
     * Subclasses must implement this to return a copy of the state with the summary dialog visibility updated.
     */
    protected abstract fun updateSummaryDialog(state: S, show: Boolean): S

    /**
     * Subclasses must implement this to indicate if the session is currently considered "finished".
     */
    protected abstract fun isSessionFinished(state: S): Boolean

    open fun onStopSession() {
        _uiState.update { updateEndSessionDialog(it, true) }
    }

    open fun dismissEndSessionDialog() {
        _uiState.update { updateEndSessionDialog(it, false) }
    }

    open fun confirmEndSession() {
        _uiState.update { 
            val stateWithoutEndDialog = updateEndSessionDialog(it, false)
            updateSaveDialog(stateWithoutEndDialog, true)
        }
    }

    open fun saveSession() {
        viewModelScope.launch {
            repository.saveSession(sessionId, isSessionFinished(_uiState.value))
            _uiState.update { 
                val stateWithoutSave = updateSaveDialog(it, false)
                updateSummaryDialog(stateWithoutSave, true)
            }
        }
    }

    open fun eraseSession(onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.eraseSession(sessionId)
            _uiState.update { updateSaveDialog(it, false) }
            onComplete()
        }
    }

    open fun dismissSaveDialog() {
        _uiState.update { updateSaveDialog(it, false) }
    }

    open fun dismissSummaryDialog(onComplete: (Long) -> Unit) {
        _uiState.update { updateSummaryDialog(it, false) }
        onComplete(sessionId)
    }
}
