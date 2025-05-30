package com.example.nearbynote.nearbyNoteMainFunction.mapBoxAPI.data

import com.example.nearbynote.BuildConfig
import kotlinx.serialization.json.Json
import javax.inject.Inject

class MapboxRepositoryImpl @Inject constructor(
    private val httpClient: MapboxHttpClient
) : MapboxRepository {

    private val token = BuildConfig.MAPBOX_API_KEY

    override suspend fun fetchAddressSuggestions(query: String): List<AddressSuggestion> {
        val url =
            "https://api.mapbox.com/geocoding/v5/mapbox.places/$query.json?access_token=$token"
        return try {
            val response = httpClient.getPlaceData(url)
            val placeResponse = Json.decodeFromString<PlaceResponse>(response)
            placeResponse.features.map {
                AddressSuggestion(
                    placeName = it.placeName,
                    latitude = it.geometry.coordinates[1],
                    longitude = it.geometry.coordinates[0]
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}

