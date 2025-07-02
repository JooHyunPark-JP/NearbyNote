package com.example.nearbynote.nearbyNoteMainFunction.mapBoxAPI.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat

import androidx.lifecycle.ViewModel
import com.example.nearbynote.nearbyNoteMainFunction.mapBoxAPI.data.SelectedNoteInfo
import com.google.android.gms.location.LocationServices
import com.mapbox.geojson.Point
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class MapboxViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val locationClient = LocationServices.getFusedLocationProviderClient(context)

    var showMap by mutableStateOf(false)

    var selectedNote by mutableStateOf<SelectedNoteInfo?>(null)

    //current user location point on the map
    var userLocation by mutableStateOf<Point?>(null)

    var showLocationDialog by mutableStateOf(false)

    //location points where user tap on the map
    var tappedLocation by mutableStateOf<Point?>(null)

    var tappedAnnotationManager: PointAnnotationManager? = null


    fun loadUserLocation(onSuccess: ((Point) -> Unit)? = null) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val point = Point.fromLngLat(it.longitude, it.latitude)
                    userLocation = point
                    onSuccess?.invoke(point)
                }
            }
        } else {
            showLocationDialog = true
        }
    }

    fun dismissLocationDialog() {
        showLocationDialog = false
    }

    fun selectNote(note: SelectedNoteInfo) {
        selectedNote = note
    }

    fun clearSelectedNote() {
        selectedNote = null
    }

    fun toggleMap(show: Boolean) {
        showMap = show
    }

    fun clearTappedLocation() {
        tappedLocation = null
    }
}