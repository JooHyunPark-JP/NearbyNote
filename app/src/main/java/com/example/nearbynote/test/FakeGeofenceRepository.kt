package com.example.nearbynote.test

import com.example.nearbynote.nearbyNoteMainFunction.geoFenceAPI.data.GeofenceEntity
import com.example.nearbynote.nearbyNoteMainFunction.geoFenceAPI.data.GeofenceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeGeofenceRepository : GeofenceRepository {
    private val geofences = mutableMapOf<String, GeofenceEntity>()
    private val flow = MutableStateFlow<List<GeofenceEntity>>(emptyList())

    override suspend fun saveGeofence(geofence: GeofenceEntity) {
        geofences[geofence.id] = geofence
        flow.value = geofences.values.toList()
    }

    override suspend fun getGeofenceById(id: String): GeofenceEntity? {
        return geofences[id]
    }

    override fun getAllGeofences(): Flow<List<GeofenceEntity>> {
        return flow
    }

    override suspend fun deleteGeofence(geofence: GeofenceEntity) {
        geofences.remove(geofence.id)
        flow.value = geofences.values.toList()
    }

    override suspend fun deleteAllGeofences() {
        TODO("Not yet implemented")
    }
}