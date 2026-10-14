package com.harrytran.claytracker.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harrytran.claytracker.data.Session
import com.harrytran.claytracker.data.SessionRepository
import com.harrytran.claytracker.data.SessionStatus
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(private val repository: SessionRepository) : ViewModel() {

    val inProgressSession: StateFlow<Session?> = repository.inProgressSession
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _showContinueDialog = MutableStateFlow(false)
    val showContinueDialog: StateFlow<Boolean> = _showContinueDialog.asStateFlow()

    private val _showSaveDialog = MutableStateFlow(false)
    val showSaveDialog: StateFlow<Boolean> = _showSaveDialog.asStateFlow()

    fun handleAddButtonClick(onNavigate: () -> Unit) {
        if (inProgressSession.value != null) {
            _showContinueDialog.value = true
        } else {
            onNavigate()
        }
    }

    fun confirmContinue(onNavigate: () -> Unit) {
        _showContinueDialog.value = false
        onNavigate()
    }

    fun declineContinue() {
        _showContinueDialog.value = false
        _showSaveDialog.value = true
    }

    fun confirmSave() {
        saveInProgressSession()
        _showSaveDialog.value = false
    }

    fun declineSave() {
        deleteInProgressSession()
        _showSaveDialog.value = false
    }

    fun dismissContinueDialog() {
        _showContinueDialog.value = false
    }

    fun dismissSaveDialog() {
        _showSaveDialog.value = false
    }

    private fun deleteInProgressSession() {
        viewModelScope.launch {
            inProgressSession.value?.let { session ->
                repository.delete(session)
            }
        }
    }

    private fun saveInProgressSession() {
        viewModelScope.launch {
            inProgressSession.value?.let { session ->
                repository.updateSession(session.copy(status = SessionStatus.INCOMPLETE))
                println("Saving incomplete session: ${session.id}")
            }
        }
    }

    // Logic housed here so multiple screens can find the right path
    fun getRouteForSession(session: Session?): String {
        if (session == null) return "new_session"

        val sportLower = session.sport.lowercase()
        return when {
            sportLower.contains("free shoot") -> "free_shoot_screen/${session.id}"
            sportLower == "skeet" -> "skeet_screen/${session.id}"
            sportLower == "trap" -> "trap_screen/${session.id}"
            sportLower == "sporting clays" -> "sporting_clays_screen/${session.id}"
            else -> "new_session"
        }
    }
}