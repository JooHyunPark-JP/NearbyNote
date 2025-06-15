package com.example.nearbynote.nearbyNoteMainFunction.savedAddress.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedAddressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(address: SavedAddressEntity)

    @Query("SELECT * FROM saved_addresses ORDER BY createdAt DESC")
    fun getAll(): Flow<List<SavedAddressEntity>>

    @Delete
    suspend fun delete(address: SavedAddressEntity)
}