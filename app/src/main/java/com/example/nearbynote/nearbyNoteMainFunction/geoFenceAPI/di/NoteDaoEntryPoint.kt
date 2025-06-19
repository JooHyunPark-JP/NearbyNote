package com.example.nearbynote.nearbyNoteMainFunction.geoFenceAPI.di

import com.example.nearbynote.nearbyNoteMainFunction.note.data.NoteDao
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface NoteDaoEntryPoint {
    fun noteDao(): NoteDao
}