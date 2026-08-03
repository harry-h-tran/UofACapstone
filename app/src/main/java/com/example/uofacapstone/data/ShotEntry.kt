package com.example.uofacapstone.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "shot_entries",
    foreignKeys = [
        ForeignKey(
            entity = Session::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["sessionId"])]
)
data class ShotEntry(
    @PrimaryKey(autoGenerate = true)
    val shotId: Long = 0,
    val sessionId: Long,
    val station: Int,
    val target: String?,
    val sublabel: String?,
    val isHit: Boolean
)
