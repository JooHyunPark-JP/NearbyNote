package com.example.nearbynote.nearbyNoteMainFunction.nearbyNoteData

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.nearbynote.nearbyNoteMainFunction.geoFenceAPI.data.GeofenceDao
import com.example.nearbynote.nearbyNoteMainFunction.geoFenceAPI.data.GeofenceEntity
import com.example.nearbynote.nearbyNoteMainFunction.note.data.NoteDao
import com.example.nearbynote.nearbyNoteMainFunction.note.data.NoteEntity

@Database(
    entities = [NoteEntity::class,
        GeofenceEntity::class],
    version = 1,
    exportSchema = false
)
abstract class NearbyNoteDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun geofenceDao(): GeofenceDao
}