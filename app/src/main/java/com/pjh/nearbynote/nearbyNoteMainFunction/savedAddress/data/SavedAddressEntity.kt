package com.pjh.nearbynote.nearbyNoteMainFunction.savedAddress.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID


@Entity(tableName = "saved_addresses")
data class SavedAddressEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val placeName: String,
    val latitude: Double,
    val longitude: Double,
    val createdAt: Long = System.currentTimeMillis()
)
