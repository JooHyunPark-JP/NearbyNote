package com.pjh.nearbynote.nearbyNoteMainFunction.geoFenceAPI.di

import com.pjh.nearbynote.nearbyNoteMainFunction.geoFenceAPI.data.GeofenceDao
import com.google.android.gms.location.GeofencingClient
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface GeofenceRebuildEntryPoint {
    fun geofenceDao(): GeofenceDao
    fun geofencingClient(): GeofencingClient
}