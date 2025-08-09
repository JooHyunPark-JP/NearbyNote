package com.pjh.nearbynote.nearbyNoteMainFunction.savedAddress.data

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SavedAddressRepositoryImpl @Inject constructor(
    private val dao: SavedAddressDao
) : SavedAddressRepository {
    override fun getAll() = dao.getAll()
    override suspend fun save(address: SavedAddressEntity) = dao.insert(address)
    override suspend fun delete(address: SavedAddressEntity) = dao.delete(address)
}