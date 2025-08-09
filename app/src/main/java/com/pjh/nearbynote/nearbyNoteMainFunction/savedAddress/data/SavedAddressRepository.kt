package com.pjh.nearbynote.nearbyNoteMainFunction.savedAddress.data

import kotlinx.coroutines.flow.Flow

interface SavedAddressRepository {
    fun getAll(): Flow<List<SavedAddressEntity>>
    suspend fun save(address: SavedAddressEntity)
    suspend fun delete(address: SavedAddressEntity)
}