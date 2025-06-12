package com.example.nearbynote.nearbyNoteMainFunction.geoFenceAPI.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nearbynote.nearbyNoteMainFunction.geoFenceAPI.data.GeofenceEntity
import com.example.nearbynote.nearbyNoteMainFunction.geoFenceAPI.data.GeofenceRepository
import com.example.nearbynote.nearbyNoteMainFunction.mapBoxAPI.data.AddressSuggestion
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GeofenceViewModel @Inject constructor(
    private val geofenceManager: GeofenceManager,
    private val geofenceRepository: GeofenceRepository
) : ViewModel() {
    private val _latitude = MutableStateFlow("")
    val latitude: StateFlow<String> = _latitude

    private val _longitude = MutableStateFlow("")
    val longitude: StateFlow<String> = _longitude

    private val _radius = MutableStateFlow("1000")
    val radius: StateFlow<String> = _radius

    private val _address = MutableStateFlow("Address will appear here")
    val address: StateFlow<String> = _address

    private val _geofenceMessage = MutableStateFlow("Waiting for Geofence status...")
    val geofenceMessage: StateFlow<String> = _geofenceMessage

    val allGeofences: Flow<List<GeofenceEntity>> = geofenceRepository.getAllGeofences()

    fun onLatitudeChanged(value: String) {
        _latitude.value = value
    }

    fun onLongitudeChanged(value: String) {
        _longitude.value = value
    }

    fun onRadiusChanged(value: String) {
        _radius.value = value
    }

    fun onFetchAddressClick() {
        viewModelScope.launch {
            val lat = latitude.value.toDoubleOrNull()
            val lng = longitude.value.toDoubleOrNull()

            if (lat != null && lng != null) {
                val result = geofenceManager.getAddressFromLatLng(lat, lng)
                _address.value = result
            } else {
                _address.value = "Invalid latitude/longitude"
            }
        }
    }

    fun onRemoveAllGeofencesClick() {
        geofenceManager.removeAllGeofences(
            onSuccess = {
                viewModelScope.launch {
                    geofenceRepository.deleteAllGeofences()
                }
                _geofenceMessage.value = "🗑️ All geofences removed."
            },
            onFailure = {
                _geofenceMessage.value = "❌ Failed to remove geofences: ${it.message}"
            },
            context = geofenceManager.context
        )
    }

    fun onSuggestionSelected(suggestion: AddressSuggestion) {
        _latitude.value = suggestion.latitude.toString()
        _longitude.value = suggestion.longitude.toString()
        _address.value = suggestion.placeName
    }

    fun updateGeofenceStatus(message: String) {
        _geofenceMessage.value = message
    }

    fun saveGeofenceToDb(
        id: String,
        name: String,
        lat: Double,
        lng: Double,
        radius: Float
    ) {
        viewModelScope.launch {
            val entity = GeofenceEntity(
                id = id,
                name = name,
                latitude = lat,
                longitude = lng,
                radius = radius,
                createdAt = System.currentTimeMillis()
            )
            geofenceRepository.saveGeofence(entity)
        }
    }

    suspend fun getGeofenceById(id: String): GeofenceEntity? {
        return geofenceRepository.getGeofenceById(id)
    }
}