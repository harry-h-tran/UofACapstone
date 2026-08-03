package com.example.uofacapstone.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.uofacapstone.data.SessionRepository
import com.example.uofacapstone.data.ShotEntry
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FreeShootUiState(
    val sessionId: Long = 0,
    val totalHits: Int = 0,
    val totalShots: Int = 0,
    val showEndSessionDialog: Boolean = false,
    val showSaveDialog: Boolean = false,
    val showSummaryDialog: Boolean = false
) {
    val totalMisses: Int get() = totalShots - totalHits
    val accuracy: Double get() = if (totalShots > 0) (totalHits.toDouble() / totalShots) * 100 else 0.0
}

class FreeShootViewModel(
    repository: SessionRepository,
    sessionId: Long
) : BaseSessionViewModel<FreeShootUiState>(repository, sessionId, FreeShootUiState(sessionId = sessionId)) {

    override fun restoreStateFromShots(state: FreeShootUiState, shots: List<ShotEntry>): FreeShootUiState {
        var hits = 0
        shots.forEach { if (it.isHit) hits++ }
        return state.copy(
            totalHits = hits,
            totalShots = shots.size
        )
    }

    override fun updateEndSessionDialog(state: FreeShootUiState, show: Boolean) = 
        state.copy(showEndSessionDialog = show)

    override fun updateSaveDialog(state: FreeShootUiState, show: Boolean) = 
        state.copy(showSaveDialog = show)

    override fun updateSummaryDialog(state: FreeShootUiState, show: Boolean) =
        state.copy(showSummaryDialog = show)

    override fun isSessionFinished(state: FreeShootUiState): Boolean = false

    fun onHit() {
        recordShot(true)
        _uiState.update { it.copy(totalHits = it.totalHits + 1, totalShots = it.totalShots + 1) }
    }

    fun onMiss() {
        recordShot(false)
        _uiState.update { it.copy(totalShots = it.totalShots + 1) }
    }

    private fun recordShot(isHit: Boolean) {
        val shotEntry = ShotEntry(
            sessionId = sessionId,
            station = 1, // Free shoot doesn't track stations specifically
            target = "Free Shoot",
            sublabel = null,
            isHit = isHit
        )
        viewModelScope.launch {
            repository.insertShot(shotEntry)
        }
    }
}

class FreeShootViewModelFactory(
    private val repository: SessionRepository,
    private val sessionId: Long
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FreeShootViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FreeShootViewModel(repository, sessionId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
