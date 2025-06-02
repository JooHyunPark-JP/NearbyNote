package com.example.nearbynote.nearbyNoteMainFunction.geoFenceAPI.data

import kotlinx.coroutines.flow.Flow

interface GeofenceRepository {
    suspend fun saveGeofence(geofence: GeofenceEntity)
    suspend fun getGeofenceById(id: String): GeofenceEntity?
    fun getAllGeofences(): Flow<List<GeofenceEntity>>
    suspend fun deleteGeofence(geofence: GeofenceEntity)
}