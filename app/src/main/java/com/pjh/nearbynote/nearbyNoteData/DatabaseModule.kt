package com.pjh.nearbynote.nearbyNoteData

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.pjh.nearbynote.nearbyNoteMainFunction.geoFenceAPI.data.GeofenceDao
import com.pjh.nearbynote.nearbyNoteMainFunction.note.data.NoteDao
import com.pjh.nearbynote.nearbyNoteMainFunction.savedAddress.data.SavedAddressDao
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
            )
            //.fallbackToDestructiveMigration(true)
         //   .addMigrations(MIGRATION_1_2)
            .build()
    }

    @Provides
    fun provideNoteDao(db: NearbyNoteDatabase): NoteDao = db.noteDao()

    @Provides
    fun provideGeofenceDao(db: NearbyNoteDatabase): GeofenceDao = db.geofenceDao()

    @Provides
    fun provideSavedAddressDao(db: NearbyNoteDatabase): SavedAddressDao = db.savedAddressDao()

    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {

        }
    }
}