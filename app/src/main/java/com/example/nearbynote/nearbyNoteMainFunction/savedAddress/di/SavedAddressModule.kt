package com.example.nearbynote.nearbyNoteMainFunction.savedAddress.di

import com.example.nearbynote.nearbyNoteMainFunction.savedAddress.data.SavedAddressDao
import com.example.nearbynote.nearbyNoteMainFunction.savedAddress.data.SavedAddressRepository
import com.example.nearbynote.nearbyNoteMainFunction.savedAddress.data.SavedAddressRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SavedAddressModule {

    @Provides
    @Singleton
    fun provideSavedAddressRepository(
        dao: SavedAddressDao
    ): SavedAddressRepository {
        return SavedAddressRepositoryImpl(dao)
    }
}