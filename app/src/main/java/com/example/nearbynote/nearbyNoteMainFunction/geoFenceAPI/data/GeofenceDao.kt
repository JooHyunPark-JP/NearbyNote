package com.example.nearbynote.nearbyNoteMainFunction.geoFenceAPI.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GeofenceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(geofence: GeofenceEntity)

    @Query("SELECT * FROM geofences WHERE id = :id")
    suspend fun getGeofenceById(id: String): GeofenceEntity?

    @Query("SELECT * FROM geofences")
    fun getAllGeofences(): Flow<List<GeofenceEntity>>

    //suspend function to get all geofences for bootcompleteReceiver
    @Query("SELECT * FROM geofences")
    suspend fun getAllGeofencesOnce(): List<GeofenceEntity>

    @Query("DELETE FROM geofences")
    suspend fun deleteAll()

    @Delete
    suspend fun delete(geofence: GeofenceEntity)



}