package com.pjh.nearbynote.nearbyNoteMainFunction.mapBoxAPI.di

import com.pjh.nearbynote.nearbyNoteMainFunction.mapBoxAPI.data.KtorMapboxHttpClient
import com.pjh.nearbynote.nearbyNoteMainFunction.mapBoxAPI.data.MapboxHttpClient
import com.pjh.nearbynote.nearbyNoteMainFunction.mapBoxAPI.data.MapboxRepository
import com.pjh.nearbynote.nearbyNoteMainFunction.mapBoxAPI.data.MapboxRepositoryImpl
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