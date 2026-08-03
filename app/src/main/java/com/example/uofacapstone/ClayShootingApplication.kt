package com.example.uofacapstone

import android.app.Application
import com.example.uofacapstone.data.AppDatabase
import com.example.uofacapstone.data.SessionRepository

class ClayShootingApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { SessionRepository(database.sessionDao(), database.shotEntryDao()) }
}
