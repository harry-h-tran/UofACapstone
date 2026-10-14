package com.harrytran.claytracker.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harrytran.claytracker.data.Session
import com.harrytran.claytracker.data.SessionRepository
import com.harrytran.claytracker.data.SessionStatus
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class StatsUiState(
    val averageSession: Session? = null,
    val bestSession: Session? = null,
    val worstSession: Session? = null,
    val skeetSessions: List<Session> = emptyList(),
    val trapSessions: List<Session> = emptyList(),
    val sportingClaysSessions: List<Session> = emptyList()
)

class StatsViewModel(private val repository: SessionRepository) : ViewModel() {

    val uiState: StateFlow<StatsUiState> = repository.allSessions
        .map { sessions ->
            val completedSessions = sessions.filter { it.status == SessionStatus.COMPLETED && it.accuracy != null }
            
            if (completedSessions.isEmpty()) {
                StatsUiState()
            } else {
                val completedWithAccuracy = completedSessions.filter { it.accuracy != null }
                val best = completedWithAccuracy.maxByOrNull { it.accuracy ?: 0.0 }
                val worst = completedWithAccuracy.minByOrNull { it.accuracy ?: 0.0 }
                
                val avgAccuracy = if (completedWithAccuracy.isNotEmpty()) {
                    completedWithAccuracy.map { it.accuracy ?: 0.0 }.average()
                } else 0.0

                val avgScore = completedSessions.map { it.score ?: 0 }.average()
                
                val avgSession = Session(
                    dateTime = System.currentTimeMillis(), // Placeholder
                    location = "Aggregate",
                    sport = "All",
                    score = avgScore.toInt(),
                    accuracy = avgAccuracy,
                    status = SessionStatus.COMPLETED
                )

                StatsUiState(
                    averageSession = avgSession,
                    bestSession = best,
                    worstSession = worst,
                    skeetSessions = completedSessions.filter { it.sport == "Skeet" }.sortedBy { it.dateTime },
                    trapSessions = completedSessions.filter { it.sport == "Trap" }.sortedBy { it.dateTime },
                    sportingClaysSessions = completedSessions.filter { it.sport == "Sporting Clays" }.sortedBy { it.dateTime }
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = StatsUiState()
        )
}
