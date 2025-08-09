package com.pjh.nearbynote.nearbyNoteData

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pjh.nearbynote.nearbyNoteMainFunction.geoFenceAPI.data.GeofenceDao
import com.pjh.nearbynote.nearbyNoteMainFunction.geoFenceAPI.data.GeofenceEntity
import com.pjh.nearbynote.nearbyNoteMainFunction.note.data.NoteDao
import com.pjh.nearbynote.nearbyNoteMainFunction.note.data.NoteEntity
import com.pjh.nearbynote.nearbyNoteMainFunction.savedAddress.data.SavedAddressDao
import com.pjh.nearbynote.nearbyNoteMainFunction.savedAddress.data.SavedAddressEntity

@Database(
    entities = [NoteEntity::class,
        GeofenceEntity::class,
        SavedAddressEntity::class],

    version = 1,
    exportSchema = false
)
abstract class NearbyNoteDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun geofenceDao(): GeofenceDao
    abstract fun savedAddressDao(): SavedAddressDao
}