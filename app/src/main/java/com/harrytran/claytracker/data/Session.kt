package com.harrytran.claytracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sessions")
data class Session(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dateTime: Long,
    val location: String,
    val sport: String,
    val notes: String? = null,
    val score: Int? = null,
    val accuracy: Double? = null,
    val status: SessionStatus = SessionStatus.INCOMPLETE
)

enum class SessionStatus(val value: Int) {
    IN_PROGRESS(0),
    COMPLETED(1),
    INCOMPLETE(2)
}
