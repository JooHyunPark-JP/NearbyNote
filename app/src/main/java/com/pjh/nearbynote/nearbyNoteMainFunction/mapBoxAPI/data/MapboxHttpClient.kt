package com.pjh.nearbynote.nearbyNoteMainFunction.mapBoxAPI.data

//make this interface for better testing enviornment
interface MapboxHttpClient {
    suspend fun getPlaceData(url: String): String
}