package com.example.nearbynote.test

import com.example.nearbynote.nearbyNoteMainFunction.mapBoxAPI.data.MapboxRepository

class FakeMapboxRepository : MapboxRepository {
    override suspend fun fetchAddressSuggestions(query: String): List<Pair<String, String>> {
        return listOf("Test Place" to "Lat: 1.0, Lon: 2.0")
    }
}