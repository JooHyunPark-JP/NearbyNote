package com.example.nearbynote.nearbyNoteMainFunction.note.di

import com.example.nearbynote.nearbyNoteMainFunction.nearbyNoteData.NearbyNoteDatabase
import com.example.nearbynote.nearbyNoteMainFunction.note.data.NoteDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object NoteModule {

    @Provides
    fun provideWorkoutDao(database: NearbyNoteDatabase): NoteDao {
        return database.noteDao()
    }
}