package com.example.nearbynote.nearbyNoteMainFunction.mapBoxAPI.data

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class PlaceResponse(
    val type: String,
    val query: List<String>,
    val features: List<PlaceFeature>
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class PlaceFeature(
    val id: String,
    val type: String,
    @SerialName("place_name")
    val placeName: String,
    val geometry: Geometry
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class Geometry(
    val coordinates: List<Double>
)

data class AddressSuggestion(
    val placeName: String,
    val latitude: Double,
    val longitude: Double
)