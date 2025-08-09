package com.pjh.nearbynote.test

import com.pjh.nearbynote.nearbyNoteMainFunction.mapBoxAPI.data.AddressSuggestion
import com.pjh.nearbynote.nearbyNoteMainFunction.mapBoxAPI.data.MapboxRepository

class FakeMapboxRepository : MapboxRepository {
    override suspend fun fetchAddressSuggestions(query: String): List<AddressSuggestion> {
        return listOf(AddressSuggestion(placeName = "Test Place", latitude = 0.0, longitude = 0.0))
    }
}