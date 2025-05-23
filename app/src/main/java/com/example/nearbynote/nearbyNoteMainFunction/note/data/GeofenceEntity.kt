package com.example.nearbynote.nearbyNoteMainFunction.note.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "geofences")
data class GeofenceEntity(
    @PrimaryKey val id: String,  // GeofencingClient 등록할 때 사용하는 고유 ID
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val radius: Float = 100f,
)