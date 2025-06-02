package com.example.nearbynote.nearbyNoteMainFunction.geoFenceAPI.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeofenceRepositoryImpl @Inject constructor(
    private val geofenceDao: GeofenceDao
) : GeofenceRepository {

    override suspend fun saveGeofence(geofence: GeofenceEntity) {
        geofenceDao.insert(geofence)
    }

    override suspend fun getGeofenceById(id: String): GeofenceEntity? {
        return geofenceDao.getGeofenceById(id)
    }

    override fun getAllGeofences(): Flow<List<GeofenceEntity>> {
        return geofenceDao.getAllGeofences()
    }

    override suspend fun deleteGeofence(geofence: GeofenceEntity) {
        geofenceDao.delete(geofence)
    }
}