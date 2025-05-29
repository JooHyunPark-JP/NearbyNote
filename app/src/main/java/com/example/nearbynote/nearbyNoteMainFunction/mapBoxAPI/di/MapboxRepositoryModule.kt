package com.example.nearbynote.nearbyNoteMainFunction.mapBoxAPI.di

import com.example.nearbynote.nearbyNoteMainFunction.mapBoxAPI.data.KtorMapboxHttpClient
import com.example.nearbynote.nearbyNoteMainFunction.mapBoxAPI.data.MapboxHttpClient
import com.example.nearbynote.nearbyNoteMainFunction.mapBoxAPI.data.MapboxRepository
import com.example.nearbynote.nearbyNoteMainFunction.mapBoxAPI.data.MapboxRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideMapboxRepository(
        httpClient: MapboxHttpClient
    ): MapboxRepository {
        return MapboxRepositoryImpl(httpClient)
    }

    @Provides
    @Singleton
    fun provideMapboxHttpClient(): MapboxHttpClient {
        return KtorMapboxHttpClient()
    }
}