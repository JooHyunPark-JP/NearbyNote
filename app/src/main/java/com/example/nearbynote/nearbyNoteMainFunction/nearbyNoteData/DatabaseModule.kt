package com.example.nearbynote.nearbyNoteMainFunction.nearbyNoteData

import android.content.Context
import androidx.room.Room
import com.example.nearbynote.nearbyNoteMainFunction.note.data.GeofenceDao
import com.example.nearbynote.nearbyNoteMainFunction.note.data.NoteDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): NearbyNoteDatabase {
        return Room.databaseBuilder(
            context,
            NearbyNoteDatabase::class.java,
            "nearby_note.db"
        ).build()
    }

    @Provides
    fun provideNoteDao(db: NearbyNoteDatabase): NoteDao = db.noteDao()

    @Provides
    fun provideGeofenceDao(db: NearbyNoteDatabase): GeofenceDao = db.geofenceDao()
}