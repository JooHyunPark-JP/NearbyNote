package com.example.nearbynote.nearbyNoteMainFunction.geoFenceAPI.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "geofences")
data class GeofenceEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val radius: Float = 100f,
    val createdAt: Long,
)