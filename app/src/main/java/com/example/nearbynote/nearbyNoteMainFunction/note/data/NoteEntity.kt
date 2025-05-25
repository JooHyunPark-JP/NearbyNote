package com.example.nearbynote.nearbyNoteMainFunction.note.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val content: String,
    val isVoice: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val locationName: String?,
    val geofenceId: String?,
)