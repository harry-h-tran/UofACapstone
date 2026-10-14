package com.harrytran.claytracker.data

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromSessionStatus(status: SessionStatus): Int {
        return status.value
    }

    @TypeConverter
    fun toSessionStatus(value: Int): SessionStatus {
        // Returns the enum entry matching the int, or a default if not found
        return SessionStatus.entries.find { it.value == value } ?: SessionStatus.INCOMPLETE
    }
}