package com.harrytran.claytracker.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harrytran.claytracker.data.Session
import com.harrytran.claytracker.data.SessionRepository
import com.harrytran.claytracker.data.SessionStatus
import kotlinx.coroutines.launch

class NewSessionDetailsViewModel(private val repository: SessionRepository) : ViewModel() {
    var location by mutableStateOf("")
        private set

    var notes by mutableStateOf("")
        private set

    val freeShootOptions = listOf("Skeet", "Trap", "Sporting Clays", "No Sport/Free Form")
    var selectedFreeShootType by mutableStateOf(freeShootOptions.last())
        private set

    fun updateLocation(newLocation: String) {
        location = newLocation
    }

    fun updateNotes(newNotes: String) {
        notes = newNotes
    }

    fun updateFreeShootType(type: String) {
        selectedFreeShootType = type
    }

    fun startSession(sport: String, onSessionStarted: (Long) -> Unit) {
        val finalSport = if (sport.lowercase() == "free shoot") {
            "Free Shoot ($selectedFreeShootType)"
        } else {
            sport
        }

        viewModelScope.launch {
            val newSession = Session(
                dateTime = System.currentTimeMillis(),
                location = location,
                sport = finalSport,
                notes = notes,
                status = SessionStatus.IN_PROGRESS
            )
            val id = repository.insert(newSession)
            onSessionStarted(id)
        }
    }

    fun getRouteForSession(sport: String, sessionId: Long): String {
        val sportLower = sport.lowercase()
        return when {
            sportLower.contains("free shoot") -> "free_shoot_screen/$sessionId"
            sportLower == "skeet" -> "skeet_screen/$sessionId"
            sportLower == "trap" -> "trap_screen/$sessionId"
            sportLower == "sporting clays" -> "sporting_clays_screen/$sessionId"
            else -> "home"
        }
    }
}
