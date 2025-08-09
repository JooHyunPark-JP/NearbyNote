package com.pjh.nearbynote.nearbyNoteMainFunction.savedAddress.di

import com.pjh.nearbynote.nearbyNoteMainFunction.savedAddress.data.SavedAddressDao
import com.pjh.nearbynote.nearbyNoteMainFunction.savedAddress.data.SavedAddressRepository
import com.pjh.nearbynote.nearbyNoteMainFunction.savedAddress.data.SavedAddressRepositoryImpl
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