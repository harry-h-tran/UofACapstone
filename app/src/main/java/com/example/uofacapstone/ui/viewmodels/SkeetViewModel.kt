package com.example.uofacapstone.ui.viewmodels

import androidx.lifecycle.viewModelScope
import com.example.uofacapstone.data.SessionRepository
import com.example.uofacapstone.data.ShotEntry
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SkeetUiState(
    val sessionId: Long = 0,
    val currentStation: Int = 1,
    val currentTargetIndex: Int = 0,
    val currentTargetLabel: String = "High",
    val totalHits: Int = 0,
    val totalShots: Int = 0,
    val isFinished: Boolean = false,
    val showOptionDialog: Boolean = false,
    val optionDialogTitle: String = "Shot Missed, Option!",
    val optionDialogMessage: String = "",
    val optionTaken: Boolean = false,
    val isOptionShot: Boolean = false,
    val showEndSessionDialog: Boolean = false,
    val showSaveDialog: Boolean = false,
    val showSummaryDialog: Boolean = false
)

class SkeetViewModel(
    repository: SessionRepository,
    sessionId: Long
) : BaseSessionViewModel<SkeetUiState>(repository, sessionId, SkeetUiState(sessionId = sessionId)) {

    override fun restoreStateFromShots(state: SkeetUiState, shots: List<ShotEntry>): SkeetUiState {
        if (shots.isEmpty()) return state

        var totalHits = 0
        var optionTaken = false
        
        shots.forEach { shot ->
            if (shot.isHit) totalHits++
            if (shot.target?.contains("(Option)") == true) {
                optionTaken = true
            }
        }

        // Recalculate current state based on number of shots (excluding options for station progression)
        val normalShotsCount = shots.count { it.target?.contains("(Option)") != true }
        
        var tempStation = 1
        var tempTargetIndex = 0
        var tempFinished = false

        repeat(normalShotsCount) {
            val targets = stationTargets[tempStation] ?: emptyList()
            tempTargetIndex++
            if (tempTargetIndex >= targets.size) {
                tempTargetIndex = 0
                tempStation++
                if (tempStation > 8) {
                    if (!optionTaken) {
                        // Should have taken 25th shot, but let's see
                        tempStation = 8
                        tempTargetIndex = 1
                    } else {
                        tempFinished = true
                        tempStation = 8
                        tempTargetIndex = (stationTargets[8]?.size ?: 1) - 1
                    }
                }
            }
        }
        
        // If option was taken as the 25th shot after all others
        if (optionTaken && normalShotsCount == 24) {
            tempFinished = true
            tempStation = 8
            tempTargetIndex = 1
        }

        val currentLabel = if (tempFinished) "Finished" else {
            stationTargets[tempStation]?.getOrNull(tempTargetIndex) ?: "Finished"
        }

        return state.copy(
            currentStation = tempStation,
            currentTargetIndex = tempTargetIndex,
            currentTargetLabel = currentLabel,
            totalHits = totalHits,
            totalShots = shots.size,
            optionTaken = optionTaken,
            isFinished = tempFinished
        )
    }

    override fun updateEndSessionDialog(state: SkeetUiState, show: Boolean) = 
        state.copy(showEndSessionDialog = show)

    override fun updateSaveDialog(state: SkeetUiState, show: Boolean) = 
        state.copy(showSaveDialog = show)

    override fun updateSummaryDialog(state: SkeetUiState, show: Boolean) =
        state.copy(showSummaryDialog = show)

    override fun isSessionFinished(state: SkeetUiState): Boolean = state.isFinished

    override fun onStopSession() {
        if (isSessionFinished(_uiState.value)) {
            saveSession()
        } else {
            super.onStopSession()
        }
    }

    private val stationTargets = mapOf(
        1 to listOf("High", "Low", "Double (High)", "Double (Low)"),
        2 to listOf("High", "Low", "Double (High)", "Double (Low)"),
        3 to listOf("High", "Low"),
        4 to listOf("High", "Low"),
        5 to listOf("High", "Low"),
        6 to listOf("High", "Low", "Double (Low)", "Double (High)"),
        7 to listOf("High", "Low", "Double (Low)", "Double (High)"),
        8 to listOf("High", "Low")
    )

    private var _pendingOptionLabel: String? = null

    fun onHit() {
        if (_uiState.value.isFinished) return
        val wasOption = _uiState.value.isOptionShot
        recordShot(true)
        if (wasOption) {
            _uiState.update { it.copy(isOptionShot = false) }
        }
        advanceLogic()
    }

    fun onMiss() {
        if (_uiState.value.isFinished) return
        val state = _uiState.value

        if (!state.optionTaken && !state.isOptionShot) {
            // First miss of the session triggers the Option logic
            recordShot(false)
            _pendingOptionLabel = state.currentTargetLabel
            _uiState.update { it.copy(optionTaken = true) }

            val targets = stationTargets[state.currentStation] ?: emptyList()
            // Check if it's the first shot of a double
            val isFirstOfDouble = state.currentTargetLabel.contains("Double") &&
                targets.getOrNull(state.currentTargetIndex + 1)?.contains("Double") == true

            if (isFirstOfDouble) {
                // Requirement: finish the double first, then go back to the miss option
                advanceLogic()
            } else {
                // Single or second of double, trigger option immediately
                _uiState.update { it.copy(
                    showOptionDialog = true,
                    optionDialogTitle = "Shot Missed, Option!",
                    optionDialogMessage = ""
                ) }
                // Stay on current target for the option shot
            }
        } else {
            // Normal miss or already took option
            val wasOption = state.isOptionShot
            recordShot(false)
            if (wasOption) {
                _uiState.update { it.copy(isOptionShot = false) }
            }
            advanceLogic()
        }
    }

    private fun recordShot(isHit: Boolean) {
        val state = _uiState.value
        val label = state.currentTargetLabel
        val shotEntry = ShotEntry(
            sessionId = sessionId,
            station = state.currentStation,
            target = label,
            sublabel = null,
            isHit = isHit
        )

        viewModelScope.launch {
            repository.insertShot(shotEntry)
        }
        
        _uiState.update { it.copy(
            totalHits = it.totalHits + (if (isHit) 1 else 0),
            totalShots = it.totalShots + 1
        ) }
    }

    fun dismissOptionDialog() {
        val state = _uiState.value
        val label = _pendingOptionLabel ?: state.currentTargetLabel
        _uiState.update { it.copy(
            showOptionDialog = false,
            isOptionShot = true,
            currentTargetLabel = "$label (Option)"
        ) }
        _pendingOptionLabel = null
    }

    private fun advanceLogic() {
        val state = _uiState.value

        // Check if we just finished the second part of a double while having a pending option
        if (_pendingOptionLabel != null) {
            val targets = stationTargets[state.currentStation] ?: emptyList()
            val isSecondOfDouble = state.currentTargetLabel.contains("Double") &&
                (state.currentTargetIndex == targets.size - 1 ||
                 targets.getOrNull(state.currentTargetIndex + 1)?.contains("Double") == false)

            if (isSecondOfDouble) {
                _uiState.update { it.copy(
                    showOptionDialog = true,
                    optionDialogTitle = "Shot Missed, Option!",
                    optionDialogMessage = ""
                ) }
                return // Stay on current index, dismissOptionDialog will update label
            }
        }

        val targetsForStation = stationTargets[state.currentStation] ?: listOf("Unknown")
        var nextTargetIndex = state.currentTargetIndex + 1
        var nextStation = state.currentStation
        var finished = false


        if (nextTargetIndex >= targetsForStation.size ) {
            nextTargetIndex = 0
            nextStation++
            if (nextStation > 8) {
                // 25th shot logic if no miss occurred
                if (!state.optionTaken && state.totalShots < 25) {
                    _uiState.update { it.copy(
                        optionTaken = true,
                        showOptionDialog = true,
                        optionDialogTitle = "No misses!",
                        optionDialogMessage = "Option: Low 8",
                        currentStation = 8,
                        currentTargetIndex = 1,
                        currentTargetLabel = "Low"
                    ) }
                    return
                } else {
                    finished = true
                    nextStation = 8
                    nextTargetIndex = targetsForStation.size - 1
                }
            }
        }

        val nextLabel = if (!finished) {
            stationTargets[nextStation]?.getOrNull(nextTargetIndex) ?: "Finished"
        } else {
            "Finished"
        }

        _uiState.update { it.copy(
            currentStation = nextStation,
            currentTargetIndex = nextTargetIndex,
            currentTargetLabel = nextLabel,
            isFinished = finished
        ) }
    }

}
