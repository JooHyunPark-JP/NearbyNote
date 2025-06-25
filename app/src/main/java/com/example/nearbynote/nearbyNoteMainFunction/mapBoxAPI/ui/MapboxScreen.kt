package com.example.nearbynote.nearbyNoteMainFunction.mapBoxAPI.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.nearbynote.nearbyNoteMainFunction.mapBoxAPI.data.AddressSuggestion
import com.example.nearbynote.nearbyNoteMainFunction.note.ui.AddressSearchSection
import com.example.nearbynote.nearbyNoteMainFunction.note.ui.NoteViewModel
import com.google.android.gms.location.LocationServices
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style

@Composable
fun MapboxScreen(
    navController: NavController,
    noteViewModel: NoteViewModel
) {
    val context = LocalContext.current
    val fusedLocationProviderClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    val addressQuery = noteViewModel.addressQuery
    val suggestions = noteViewModel.suggestions
    val isSearching = noteViewModel.isSearching

    val selectedSuggestion = remember { mutableStateOf<AddressSuggestion?>(null) }
    var showMap by remember { mutableStateOf(false) }
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var showLocationDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        noteViewModel.addressQuery = ""
        noteViewModel.suggestions = emptyList()
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
                selectedSuggestion.value = suggestion
                noteViewModel.addressQuery = suggestion.placeName
                noteViewModel.suggestions = emptyList()
                showMap = true
            },
            isAddressSearching = isSearching,
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
                    text = "\uD83D\uDCCD Please choose the address via textfield\nor click the map icon to load the map",
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
                        showMap = true
                        if (ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            fusedLocationProviderClient.lastLocation
                                .addOnSuccessListener { location ->
                                    location?.let {
                                        mapView?.mapboxMap?.setCamera(
                                            CameraOptions.Builder()
                                                .center(
                                                    com.mapbox.geojson.Point.fromLngLat(
                                                        it.longitude,
                                                        it.latitude
                                                    )
                                                )
                                                .zoom(14.0)
                                                .build()
                                        )
                                    }
                                }
                        } else {
                            showLocationDialog = true
                        }
                    },
                    modifier = Modifier
                        .size(64.dp)
                        .background(MaterialTheme.colorScheme.primary, shape = CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
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
                            mv.mapboxMap.loadStyle(Style.STANDARD)
                        }
                    }
                )

                FloatingActionButton(
                    onClick = {
                        if (ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            fusedLocationProviderClient.lastLocation
                                .addOnSuccessListener { location ->
                                    location?.let {
                                        mapView?.mapboxMap?.setCamera(
                                            CameraOptions.Builder()
                                                .center(
                                                    com.mapbox.geojson.Point.fromLngLat(
                                                        it.longitude,
                                                        it.latitude
                                                    )
                                                )
                                                .zoom(14.0)
                                                .build()
                                        )
                                    }
                                }
                        } else {
                            showLocationDialog = true
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = "My Location"
                    )
                }
            }
        }
    }

    if (showLocationDialog) {
        AlertDialog(
            onDismissRequest = { showLocationDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    showLocationDialog = false
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                }) {
                    Text("Open Settings")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLocationDialog = false }) {
                    Text("Cancel")
                }
            },
            title = { Text("Location permission required") },
            text = {
                Text(
                    "\uD83D\uDCCD To center the map on your location, please allow location permission in settings."
                )
            }
        )
    }
}
