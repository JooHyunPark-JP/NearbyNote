package com.example.nearbynote.nearbyNoteMainFunction.mapBoxAPI.data


interface MapboxRepository {
    suspend fun fetchAddressSuggestions(query: String): List<AddressSuggestion>
}