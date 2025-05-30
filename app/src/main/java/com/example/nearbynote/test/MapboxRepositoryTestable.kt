package com.example.nearbynote.test

import com.example.nearbynote.nearbyNoteMainFunction.mapBoxAPI.data.AddressSuggestion
import com.example.nearbynote.nearbyNoteMainFunction.mapBoxAPI.data.MapboxRepository

class FakeMapboxRepository : MapboxRepository {
    override suspend fun fetchAddressSuggestions(query: String): List<AddressSuggestion> {
        return listOf(AddressSuggestion(placeName = "Test Place", latitude = 0.0, longitude = 0.0))
    }
}