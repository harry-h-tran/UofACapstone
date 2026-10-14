package com.harrytran.claytracker.ui.viewmodels

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.harrytran.claytracker.data.SessionRepository
import com.harrytran.claytracker.data.ShotEntry
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TrapUiState(
    val sessionId: Long = 0,
    val currentStation: Int = 1,
    val totalHits: Int = 0,
    val totalShots: Int = 0,
    val shotsAtCurrentStation: Int = 0,
    val isSessionComplete: Boolean = false,
    val showEndSessionDialog: Boolean = false,
    val showSaveDialog: Boolean = false,
    val showSummaryDialog: Boolean = false
)

class TrapViewModel(
    repository: SessionRepository,
    sessionId: Long
) : BaseSessionViewModel<TrapUiState>(repository, sessionId, TrapUiState(sessionId = sessionId)) {

    override fun restoreStateFromShots(state: TrapUiState, shots: List<ShotEntry>): TrapUiState {
        if (shots.isEmpty()) return state

        var totalHits = 0
        shots.forEach { if (it.isHit) totalHits++ }

        val totalShots = shots.size
        val nextStation = (totalShots / 5) + 1
        val nextShotsAtStation = totalShots % 5
        val isComplete = totalShots >= 25

        return state.copy(
            totalHits = totalHits,
            totalShots = totalShots,
            currentStation = if (isComplete) 5 else nextStation,
            shotsAtCurrentStation = if (isComplete) 0 else nextShotsAtStation,
            isSessionComplete = isComplete
        )
    }

    override fun updateEndSessionDialog(state: TrapUiState, show: Boolean) = 
        state.copy(showEndSessionDialog = show)

    override fun updateSaveDialog(state: TrapUiState, show: Boolean) = 
        state.copy(showSaveDialog = show)

    override fun updateSummaryDialog(state: TrapUiState, show: Boolean) =
        state.copy(showSummaryDialog = show)

    override fun isSessionFinished(state: TrapUiState): Boolean = state.isSessionComplete

    override fun onStopSession() {
        if (isSessionFinished(_uiState.value)) {
            saveSession()
        } else {
            super.onStopSession()
        }
    }

    fun onHit() {
        if (_uiState.value.isSessionComplete) return
        recordShot(isHit = true)
        advanceLogic(isHit = true)
    }

    fun onMiss() {
        if (_uiState.value.isSessionComplete) return
        recordShot(isHit = false)
        advanceLogic(isHit = false)
    }

    /**
     * Handles the business logic for advancing the session state (UI state).
     */
    private fun advanceLogic(isHit: Boolean) {
        _uiState.update { state ->
            val newTotalHits = if (isHit) state.totalHits + 1 else state.totalHits
            val newTotalShots = state.totalShots + 1
            val nextShotsAtStation = state.shotsAtCurrentStation + 1
            
            var nextStation = state.currentStation
            var nextShotsAtStationAdjusted = nextShotsAtStation
            var isComplete = state.isSessionComplete

            // Trap Logic: 5 shots per station, 5 stations total
            if (nextShotsAtStation >= 5) {
                if (nextStation < 5) {
                    nextStation++
                    nextShotsAtStationAdjusted = 0
                } else {
                    isComplete = true
                }
            }

            state.copy(
                totalHits = newTotalHits,
                totalShots = newTotalShots,
                currentStation = nextStation,
                shotsAtCurrentStation = nextShotsAtStationAdjusted,
                isSessionComplete = isComplete
            )
        }

        if (_uiState.value.isSessionComplete) {
            handleSessionCompletion()
        }
    }

    private fun recordShot(isHit: Boolean) {
        val currentState = _uiState.value
        val shotEntry = ShotEntry(
            sessionId = currentState.sessionId,
            station = currentState.currentStation,
            target = "Shot ${currentState.shotsAtCurrentStation + 1}",
            sublabel = null,
            isHit = isHit
        )

        viewModelScope.launch {
            try {
                repository.insertShot(shotEntry)
            } catch (e: Exception) {
                Log.e("TrapViewModel", "Error recording shot to database", e)
            }
        }
    }

    private fun handleSessionCompletion() {
        Log.d("TrapViewModel", "Session Complete! Final Score: ${_uiState.value.totalHits}/${_uiState.value.totalShots}")
    }

    fun updateStation(newStation: Int) {
        _uiState.update { it.copy(currentStation = newStation, shotsAtCurrentStation = 0) }
    }

}
