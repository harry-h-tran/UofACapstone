package com.example.uofacapstone.data

import kotlinx.coroutines.flow.Flow

class SessionRepository(
    private val sessionDao: SessionDao,
    private val shotEntryDao: ShotEntryDao
) {
    val allSessions: Flow<List<Session>> = sessionDao.getAllSessions()

    val recentSessions: Flow<List<Session>> = sessionDao.getRecentSessions()

    suspend fun insert(session: Session): Long {
        return sessionDao.insertSession(session)
    }

    suspend fun updateSession(session: Session) {
        sessionDao.updateSession(session)
    }

    suspend fun insertShot(shotEntry: ShotEntry) {
        shotEntryDao.insertShot(shotEntry)
    }

    suspend fun delete(session: Session) {
        shotEntryDao.deleteShotsForSession(session.id)
        sessionDao.deleteSession(session)
    }

    suspend fun getSessionById(id: Long): Session? {
        return sessionDao.getSessionById(id)
    }

    suspend fun saveSession(sessionId: Long, isCompleted: Boolean) {
        val session = sessionDao.getSessionById(sessionId) ?: return
        val shots = shotEntryDao.getShotsForSessionSync(sessionId)
        
        val totalShots = shots.size
        val totalHits = shots.count { it.isHit }
        val accuracy = if (totalShots > 0) (totalHits.toDouble() / totalShots) else 0.0
        
        val updatedSession = session.copy(
            score = if (isCompleted) totalHits else null,
            accuracy = accuracy,
            status = if (isCompleted) SessionStatus.COMPLETED else SessionStatus.INCOMPLETE
        )
        sessionDao.updateSession(updatedSession)
    }

    suspend fun eraseSession(sessionId: Long) {
        val session = sessionDao.getSessionById(sessionId)
        if (session != null) {
            sessionDao.deleteSession(session) //Shot Entries will get deleted too
        }
    }

    val inProgressSession: Flow<Session?> = sessionDao.getInProgressSession()

    fun getShotsForSession(sessionId: Long): Flow<List<ShotEntry>> {
        return shotEntryDao.getShotsForSession(sessionId)
    }

    fun getSessionByIdFlow(id: Long): Flow<Session?> {
        return sessionDao.getSessionByIdFlow(id)
    }
}
