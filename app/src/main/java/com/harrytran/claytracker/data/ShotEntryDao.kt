package com.harrytran.claytracker.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ShotEntryDao {
    @Query("SELECT * FROM shot_entries WHERE sessionId = :sessionId ORDER BY shotId ASC")
    fun getShotsForSession(sessionId: Long): Flow<List<ShotEntry>>

    @Query("SELECT * FROM shot_entries WHERE sessionId = :sessionId")
    suspend fun getShotsForSessionSync(sessionId: Long): List<ShotEntry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShot(shot: ShotEntry)

    @Update
    suspend fun updateShot(shot: ShotEntry)

    @Delete
    suspend fun deleteShot(shot: ShotEntry)

    @Query("DELETE FROM shot_entries WHERE sessionId = :sessionId")
    suspend fun deleteShotsForSession(sessionId: Long)
}
