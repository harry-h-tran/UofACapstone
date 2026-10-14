package com.harrytran.claytracker.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.harrytran.claytracker.data.SessionRepository

class ViewModelFactory(private val repository: SessionRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(NewSessionDetailsViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                NewSessionDetailsViewModel(repository) as T
            }
            modelClass.isAssignableFrom(NewSessionViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                NewSessionViewModel() as T
            }
            modelClass.isAssignableFrom(StatsViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                StatsViewModel(repository) as T
            }
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                MainViewModel(repository) as T
            }
            modelClass.isAssignableFrom(SessionListViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                SessionListViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}

class SkeetViewModelFactory(
    private val repository: SessionRepository,
    private val sessionId: Long
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SkeetViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SkeetViewModel(repository, sessionId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class SportingClaysViewModelFactory(
    private val repository: SessionRepository,
    private val sessionId: Long
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SportingClaysViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SportingClaysViewModel(repository, sessionId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class TrapViewModelFactory(
    private val repository: SessionRepository,
    private val sessionId: Long
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TrapViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TrapViewModel(repository, sessionId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
