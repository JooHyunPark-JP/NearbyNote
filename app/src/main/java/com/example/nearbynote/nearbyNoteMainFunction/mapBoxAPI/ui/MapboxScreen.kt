package com.example.nearbynote.nearbyNoteMainFunction.mapBoxAPI.ui

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.scale
import androidx.navigation.NavController
import com.example.nearbynote.R
import com.example.nearbynote.nearbyNoteMainFunction.geoFenceAPI.ui.GeofenceViewModel
import com.example.nearbynote.nearbyNoteMainFunction.mapBoxAPI.data.SelectedNoteInfo
import com.example.nearbynote.nearbyNoteMainFunction.note.ui.AddressSearchSection
import com.example.nearbynote.nearbyNoteMainFunction.note.ui.NoteViewModel
import com.example.nearbynote.nearbyNoteNav.Screen
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import java.text.DateFormat
import java.util.Date
import kotlin.math.cos
import kotlin.math.pow

@Composable
fun MapboxScreen(
    navController: NavController,
    noteViewModel: NoteViewModel,
    geofenceViewModel: GeofenceViewModel,
    mapboxViewModel: MapboxViewModel
) {
    val notes by noteViewModel.notes.collectAsState()
    val geofences by geofenceViewModel.allGeofences.collectAsState(initial = emptyList())
    val context = LocalContext.current

    val addressQuery = noteViewModel.addressQuery
    val suggestions = noteViewModel.suggestions
    val isSearching = noteViewModel.isSearching
    val showMap = mapboxViewModel.showMap
    val selectedNote = mapboxViewModel.selectedNote
    val userLocation = mapboxViewModel.userLocation
    val showLocationDialog = mapboxViewModel.showLocationDialog

    var mapView by remember { mutableStateOf<MapView?>(null) }

    LaunchedEffect(Unit) {
        noteViewModel.addressQuery = ""
        noteViewModel.suggestions = emptyList()
        mapboxViewModel.showMap = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        AddressSearchSection(
            addressQuery = addressQuery,
            onQueryChange = { noteViewModel.onQueryChanged(it) },
            suggestions = suggestions,
            onSuggestionSelected = { suggestion ->
                noteViewModel.addressQuery = suggestion.placeName
                noteViewModel.suggestions = emptyList()
                mapboxViewModel.toggleMap(true)
            },
            isAddressSearching = isSearching
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (!showMap) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "📍 Please choose the address via textfield\nor click the map icon to load the map",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = {
                        mapboxViewModel.toggleMap(true)
                        mapboxViewModel.loadUserLocation()
                    },
                    modifier = Modifier
                        .size(64.dp)
                        .background(MaterialTheme.colorScheme.primary, shape = CircleShape)
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = "Load Map",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                AndroidView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp),
                    factory = { context ->
                        MapView(context).also { mv ->
                            mapView = mv
                            mv.mapboxMap.loadStyle(Style.STANDARD) { style ->
                                style.addImage(
                                    "current-location-icon",
                                    BitmapFactory.decodeResource(
                                        context.resources,
                                        R.drawable.current_location_icon
                                    )
                                        .scale(100, 100, false)
                                )
                                style.addImage(
                                    "note-marker-icon",
                                    BitmapFactory.decodeResource(
                                        context.resources,
                                        R.drawable.note_icon
                                    )
                                        .scale(62, 62, false)
                                )

                                mapboxViewModel.loadUserLocation { point ->
                                    mapView?.mapboxMap?.setCamera(
                                        CameraOptions.Builder().center(point).zoom(14.0).build()
                                    )
                                }

                            }
                        }
                    }
                )

                LaunchedEffect(userLocation) {
                    userLocation?.let { point ->
                        mapView?.let { mv ->
                            mv.mapboxMap.setCamera(
                                CameraOptions.Builder().center(point).zoom(14.0).build()
                            )

                            val annotationApi = mv.annotations
                            val pointAnnotationManager =
                                annotationApi.createPointAnnotationManager()

                            pointAnnotationManager.create(
                                PointAnnotationOptions().withPoint(point)
                                    .withIconImage("current-location-icon")
                            )

                            notes.forEach { note ->
                                geofences.find { it.id == note.geofenceId }?.let { geofence ->
                                    val notePoint =
                                        Point.fromLngLat(geofence.longitude, geofence.latitude)
                                    val marker = pointAnnotationManager.create(
                                        PointAnnotationOptions().withPoint(notePoint)
                                            .withIconImage("note-marker-icon")
                                    )

                                    pointAnnotationManager.addClickListener { clicked ->
                                        if (clicked == marker) {
                                            mapboxViewModel.selectNote(
                                                SelectedNoteInfo(
                                                    id = note.id,
                                                    content = note.content,
                                                    createdAt = note.createdAt,
                                                    radius = geofence.radius,
                                                    address = geofence.addressName
                                                )
                                            )
                                            true
                                        } else false
                                    }
                                }
                            }
                        }
                    }
                }

                FloatingActionButton(
                    onClick = { mapboxViewModel.loadUserLocation() },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                ) {
                    Icon(Icons.Default.Done, contentDescription = "My Location")
                }
            }
        }
    }

    if (showLocationDialog) {
        AlertDialog(
            onDismissRequest = { mapboxViewModel.dismissLocationDialog() },
            confirmButton = {
                TextButton(onClick = {
                    mapboxViewModel.dismissLocationDialog()
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                }) {
                    Text("Open Settings")
                }
            },
            dismissButton = {
                TextButton(onClick = { mapboxViewModel.dismissLocationDialog() }) {
                    Text("Cancel")
                }
            },
            title = { Text("Location permission required") },
            text = {
                Text("📍 To center the map on your location, please allow location permission in settings.")
            }
        )
    }

    selectedNote?.let { note ->
        AlertDialog(
            onDismissRequest = { mapboxViewModel.clearSelectedNote() },
            title = { Text("📝 About this note...") },
            text = {
                Column {
                    Text("Note: \n${note.content}")
                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider()
                    Spacer(Modifier.height(4.dp))
                    Text("Radius: ${note.radius}m")
                    Spacer(Modifier.height(4.dp))
                    Text("Location: ${note.address}")
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Created: " + DateFormat.getDateTimeInstance().format(Date(note.createdAt))
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    navController.navigate(Screen.WriteNoteScreen.routeWithNoteId(note.id))
                    mapboxViewModel.clearSelectedNote()
                }) {
                    Text("Go to note")
                }
            },
            dismissButton = {
                TextButton(onClick = { mapboxViewModel.clearSelectedNote() }) {
                    Text("Close")
                }
            }
        )
    }
}


fun convertMetersToPixelsAtLatitude(
    radius: Double,
    latitude: Double,
    zoom: Double
): Double {
    val earthCircumference = 40075017.0 // meters
    val latitudeRadians = Math.toRadians(latitude)
    val metersPerPixel = earthCircumference * cos(latitudeRadians) / (256 * 2.0.pow(zoom))
    return radius / metersPerPixel
}
