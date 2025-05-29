package com.example.nearbynote.nearbyNoteMainFunction.mapBoxAPI.data

import kotlinx.serialization.json.Json
import javax.inject.Inject


class MapboxRepositoryImpl @Inject constructor(
    private val httpClient: MapboxHttpClient
) : MapboxRepository {

    private val token =
        "pk.eyJ1IjoicGpoMDYyOSIsImEiOiJjbWFvaWRocWQwNm9uMmxvZno0amgzZjY0In0.KUt35ham2WyiV9rYoEX_Zg"

    override suspend fun fetchAddressSuggestions(query: String): List<Pair<String, String>> {
        val url =
            "https://api.mapbox.com/geocoding/v5/mapbox.places/$query.json?access_token=$token"
        return try {
            val response = httpClient.getPlaceData(url)
            val placeResponse = Json.decodeFromString<PlaceResponse>(response)
            placeResponse.features.map {
                val lat = it.geometry.coordinates[1]
                val lon = it.geometry.coordinates[0]
                it.placeName to "Lat: $lat, Lon: $lon"
            }
        } catch (e: Exception) {
            // Log.d("MapboxAPI", "Error: ${e.message}")
            emptyList()
        }
    }
}

