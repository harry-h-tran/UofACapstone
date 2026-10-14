package com.harrytran.claytracker

import android.app.Application
import com.harrytran.claytracker.data.AppDatabase
import com.harrytran.claytracker.data.SessionRepository

class ClayShootingApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { SessionRepository(database.sessionDao(), database.shotEntryDao()) }
}
