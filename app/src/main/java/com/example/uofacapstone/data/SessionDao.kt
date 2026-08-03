package com.example.uofacapstone.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Query("SELECT * FROM sessions ORDER BY dateTime DESC")
    fun getAllSessions(): Flow<List<Session>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: Session): Long

    @Update
    suspend fun updateSession(session: Session)

    @Delete
    suspend fun deleteSession(session: Session)

    @Query("SELECT * FROM sessions WHERE id = :id")
    suspend fun getSessionById(id: Long): Session?

    @Query("SELECT * FROM sessions WHERE id = :id")
    fun getSessionByIdFlow(id: Long): Flow<Session?>

    @Query("SELECT * FROM sessions WHERE status IN (1, 2) ORDER BY dateTime DESC")
    fun getRecentSessions(): Flow<List<Session>>

    @Query("SELECT * FROM sessions WHERE status = 0 ORDER BY dateTime DESC LIMIT 1")
    fun getInProgressSession(): Flow<Session?>
}
