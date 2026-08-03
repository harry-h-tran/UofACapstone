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

data class SportingClaysUiState(
    val sessionId: Long = 0,
    val station: Int = 1,
    val target: Int = 1,
    val shotType: String = "Single",
    val pairType: String = "True Pair",
    val totalHits: Int = 0,
    val totalShots: Int = 0,
    val isFinished: Boolean = false,
    val showEndSessionDialog: Boolean = false,
    val showSaveDialog: Boolean = false,
    val showSummaryDialog: Boolean = false
)

class SportingClaysViewModel(
    repository: SessionRepository,
    sessionId: Long
) : BaseSessionViewModel<SportingClaysUiState>(repository, sessionId, SportingClaysUiState(sessionId = sessionId)) {

    override fun restoreStateFromShots(state: SportingClaysUiState, shots: List<ShotEntry>): SportingClaysUiState {
        if (shots.isEmpty()) return state

        var totalHits = 0
        shots.forEach { if (it.isHit) totalHits++ }

        // Sporting Clays is more free-form, but we can restore the last used station/target
        val lastShot = shots.last()
        
        return state.copy(
            totalHits = totalHits,
            totalShots = shots.size,
            station = lastShot.station,
            target = lastShot.target?.toIntOrNull() ?: 1
        )
    }

    override fun updateEndSessionDialog(state: SportingClaysUiState, show: Boolean) = 
        state.copy(showEndSessionDialog = show)

    override fun updateSaveDialog(state: SportingClaysUiState, show: Boolean) = 
        state.copy(showSaveDialog = show)

    override fun updateSummaryDialog(state: SportingClaysUiState, show: Boolean) =
        state.copy(showSummaryDialog = show)

    override fun isSessionFinished(state: SportingClaysUiState): Boolean = state.isFinished

    fun completeSession(isFinished: Boolean) {
        viewModelScope.launch {
            repository.saveSession(sessionId, isFinished)
            _uiState.update { 
                it.copy(
                    showEndSessionDialog = false,
                    showSaveDialog = false,
                    showSummaryDialog = true,
                    isFinished = isFinished
                )
            }
        }
    }

    fun updateStation(newStation: Int) {
        _uiState.update { it.copy(station = newStation, target = 1, shotType = "Single") }
    }

    fun updateTarget(newTarget: Int) {
        _uiState.update { it.copy(target = newTarget, shotType = "Single") }
    }

    fun updateShotType(newShotType: String) {
        _uiState.update { it.copy(shotType = newShotType) }
    }

    fun updatePairType(newPairType: String) {
        _uiState.update { it.copy(pairType = newPairType) }
    }

    fun onHit() {
        val state = _uiState.value
        recordShot(true)
        _uiState.update { it.copy(totalHits = it.totalHits + 1, totalShots = it.totalShots + 1) }
    }

    fun onMiss() {
        val state = _uiState.value
        recordShot(false)
        _uiState.update { it.copy(totalShots = it.totalShots + 1) }
    }

    private fun recordShot(isHit: Boolean) {
        val state = _uiState.value
        val shotEntry = ShotEntry(
            sessionId = sessionId,
            station = state.station,
            target = state.target.toString(),
            sublabel = if (state.shotType == "Single") state.shotType else "${state.shotType} (${state.pairType})",
            isHit = isHit
        )
        viewModelScope.launch {
            repository.insertShot(shotEntry)
        }
    }

}
