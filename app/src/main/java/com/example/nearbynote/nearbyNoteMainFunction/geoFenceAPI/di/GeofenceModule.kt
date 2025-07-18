package com.example.nearbynote.nearbyNoteMainFunction.geoFenceAPI.di

import android.content.Context
import com.example.nearbynote.nearbyNoteMainFunction.geoFenceAPI.data.GeofenceDao
import com.example.nearbynote.nearbyNoteMainFunction.geoFenceAPI.data.GeofenceRepository
import com.example.nearbynote.nearbyNoteMainFunction.geoFenceAPI.data.GeofenceRepositoryImpl
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GeofenceModule {

    @Provides
    @Singleton
    fun provideGeofencingClient(@ApplicationContext context: Context): GeofencingClient {
        return LocationServices.getGeofencingClient(context)
    }

    @Provides
    @Singleton
    fun provideGeofenceRepository(
        geofenceDao: GeofenceDao
    ): GeofenceRepository {
        return GeofenceRepositoryImpl(geofenceDao)
    }
}