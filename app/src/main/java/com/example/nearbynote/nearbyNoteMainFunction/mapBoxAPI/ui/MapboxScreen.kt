package com.example.nearbynote.nearbyNoteMainFunction.mapBoxAPI.ui

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.scale
import androidx.navigation.NavController
import com.example.nearbynote.R
import com.example.nearbynote.nearbyNoteMainFunction.geoFenceAPI.ui.GeofenceManager
import com.example.nearbynote.nearbyNoteMainFunction.geoFenceAPI.ui.GeofenceViewModel
import com.example.nearbynote.nearbyNoteMainFunction.mapBoxAPI.data.SelectedNoteInfo
import com.example.nearbynote.nearbyNoteMainFunction.note.ui.AddressSearchSection
import com.example.nearbynote.nearbyNoteMainFunction.note.ui.NoteViewModel
import com.example.nearbynote.nearbyNoteNav.Screen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.common.Cancelable
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.Date
import kotlin.math.cos
import kotlin.math.pow

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapboxScreen(
    navController: NavController,
    noteViewModel: NoteViewModel,
    geofenceViewModel: GeofenceViewModel,
    mapboxViewModel: MapboxViewModel,
    geofenceManager: GeofenceManager
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

    var cameraSubscription: Cancelable? = null

    var mapView by remember { mutableStateOf<MapView?>(null) }

    var hasLaunchedPermissionRequest by rememberSaveable { mutableStateOf(false) }

    val coarseLocationPermissionState = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    val canOpenMapBoxMap = remember { mutableStateOf(false) }
    val mapViewHolder = remember { mutableStateOf<MapView?>(null) }

    LaunchedEffect(Unit) {
        noteViewModel.addressQuery = ""
        noteViewModel.suggestions = emptyList()
        mapboxViewModel.showMap = false
        mapboxViewModel.tappedLocation = null

        if (!hasLaunchedPermissionRequest) {
            hasLaunchedPermissionRequest = true

            if (
                coarseLocationPermissionState.status is PermissionStatus.Denied
            ) {
                coarseLocationPermissionState.launchPermissionRequest()
            }
        }
    }

    LaunchedEffect(showMap) {
        if (showMap && mapViewHolder.value == null) {
            if (isOpenGL3Supported(context)) {
                mapViewHolder.value = MapView(context)
            } else {
                canOpenMapBoxMap.value = true
            }
        }
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

                // Show map
                mapboxViewModel.toggleMap(true)

                // Move the map to the address
                CoroutineScope(Dispatchers.Main).launch {
                    val result = geofenceManager.getLatLngFromAddress(suggestion.placeName)

                    result.fold(
                        onSuccess = { address ->
                            if (address != null) {
                                val point = Point.fromLngLat(address.longitude, address.latitude)
                                mapboxViewModel.tappedLocation = point
                                mapView?.mapboxMap?.setCamera(
                                    CameraOptions.Builder()
                                        .center(point)
                                        .zoom(17.0)
                                        .build()
                                )
                            } else {
                                Toast.makeText(
                                    context,
                                    "No address found for this location.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        onFailure = {
                            Toast.makeText(
                                context,
                                "Failed to get address. Check your internet connection.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                }
            },
            isAddressSearching = isSearching,
            noteViewModel = noteViewModel
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
                    text = "📍 Search an address or tap the map icon below to open the map.",
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
                    Image(
                        painter = painterResource(id = R.drawable.mapicon),
                        contentDescription = "My Location",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        } else if (canOpenMapBoxMap.value) {
            // Show message that this device can't open the map
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⚠️ Map couldn't be loaded. This device may not support OpenGL ES 3.0.",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                mapViewHolder.value?.let { safeMapView ->
                    AndroidView(
                        modifier = Modifier
                            .fillMaxWidth(),
                        factory = { context ->
                            safeMapView.also { mv ->
                                mapView = mv
                                mv.mapboxMap.loadStyle(Style.STANDARD) { style ->
                                    mv.gestures.addOnMapClickListener { point ->
                                        mapboxViewModel.tappedLocation = point
                                        true
                                    }

                                    mv.gestures.addOnMoveListener(
                                        object : OnMoveListener {
                                            override fun onMoveBegin(detector: MoveGestureDetector) {
                                                mapboxViewModel.onUserInteractionStart()
                                            }

                                            override fun onMove(detector: MoveGestureDetector): Boolean =
                                                false

                                            override fun onMoveEnd(detector: MoveGestureDetector) {
                                                mapboxViewModel.onUserInteractionEnd()
                                            }
                                        }
                                    )

                                    // clear the message "Create a note here" on map when zoom in/out or move map
                                    cameraSubscription = mv.mapboxMap.subscribeCameraChanged {
                                        if (mapboxViewModel.isUserInteractingWithMap) {
                                            mapboxViewModel.clearTappedLocation()
                                        }
                                    }


                                    style.addImage(
                                        "current-location-icon",
                                        BitmapFactory.decodeResource(
                                            context.resources,
                                            R.drawable.current_location_icon
                                        )
                                            .scale(62, 62, false)
                                    )
                                    style.addImage(
                                        "location-marker-icon",
                                        BitmapFactory.decodeResource(
                                            context.resources,
                                            R.drawable.location_pin
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
                                            CameraOptions.Builder().center(point).zoom(17.0)
                                                .build()
                                        )
                                    }

                                }
                            }
                        }
                    )
                }

                //top right icon explanation on the map
                Column(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(
                            Color.White.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.current_location_icon),
                            contentDescription = "Current Location",
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Current Location", style = MaterialTheme.typography.labelSmall)
                    }


                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.location_pin),
                            contentDescription = "Selected Location",
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Selected Location", style = MaterialTheme.typography.labelSmall)
                    }


                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.note_icon),
                            contentDescription = "Note",
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Note", style = MaterialTheme.typography.labelSmall)
                    }
                }

                LaunchedEffect(userLocation) {
                    userLocation?.let { point ->
                        mapView?.let { mv ->
                            mv.mapboxMap.setCamera(
                                CameraOptions.Builder().center(point).zoom(17.0).build()
                            )

                            val annotationApi = mv.annotations
                            val pointAnnotationManager =
                                annotationApi.createPointAnnotationManager()

                            pointAnnotationManager.create(
                                PointAnnotationOptions().withPoint(point)
                                    .withIconImage("current-location-icon")
                            )

                            notes.forEach { note ->
                                geofences.find { it.id == note.geofenceId }
                                    ?.let { geofence ->
                                        val notePoint =
                                            Point.fromLngLat(
                                                geofence.longitude,
                                                geofence.latitude
                                            )
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
                                                        updateAt = note.updatedAt,
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

                LaunchedEffect(mapboxViewModel.tappedLocation) {
                    val tappedPoint =
                        mapboxViewModel.tappedLocation ?: return@LaunchedEffect
                    mapView?.let { mv ->
                        val annotationApi = mv.annotations

                        mapboxViewModel.tappedAnnotationManager?.deleteAll()
                        val tappedManager = annotationApi.createPointAnnotationManager()
                        mapboxViewModel.tappedAnnotationManager = tappedManager


                        tappedManager.create(
                            PointAnnotationOptions()
                                .withPoint(tappedPoint)
                                .withIconImage("location-marker-icon")
                        )
                    }
                }

                FloatingActionButton(
                    onClick = {
                        mapboxViewModel.loadUserLocation { point ->
                            mapView?.mapboxMap?.setCamera(
                                CameraOptions.Builder().center(point).zoom(17.0).build()
                            )
                        }
                    },
                    shape = CircleShape,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = "Load Map",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }


                mapboxViewModel.tappedLocation?.let { point ->
                    val density = LocalDensity.current
                    val screenCoordinate = mapView?.mapboxMap?.pixelForCoordinate(point)

                    screenCoordinate?.let { coordinate ->

                        val xDp = with(density) { coordinate.x.toFloat().toDp() }
                        val yDp = with(density) { coordinate.y.toFloat().toDp() }
                        Box(
                            modifier = Modifier
                                .absoluteOffset(
                                    x = xDp,
                                    y = yDp
                                )
                                .background(Color.White, RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {

                            TextButton(onClick = {
                                val tappedPoint = mapboxViewModel.tappedLocation
                                if (tappedPoint != null) {
                                    CoroutineScope(Dispatchers.Main).launch {
                                        val result = geofenceManager.getAddressFromLatLng(
                                            tappedPoint.latitude(),
                                            tappedPoint.longitude()
                                        )

                                        result.fold(
                                            onSuccess = { addressText ->
                                                noteViewModel.addressQuery = addressText
                                                noteViewModel.addressLatitude =
                                                    tappedPoint.latitude()
                                                noteViewModel.addressLongitude =
                                                    tappedPoint.longitude()

                                                geofenceViewModel.onLatitudeChanged(
                                                    tappedPoint.latitude().toString()
                                                )
                                                geofenceViewModel.onLongitudeChanged(
                                                    tappedPoint.longitude().toString()
                                                )

                                                noteViewModel.preserveMapLocation = true
                                                noteViewModel.isAddressSelected = true

                                                navController.navigate(
                                                    Screen.WriteNoteScreen.routeWithNoteId(
                                                        null
                                                    )
                                                )
                                            },
                                            onFailure = {
                                                Toast.makeText(
                                                    context,
                                                    "Check your internet connection to create a note here!",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        )
                                    }
                                }
                            }) {
                                Text("Create a note here")
                            }
                        }
                    }
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
                    Text(text = note.content)
                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider()
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = note.address,
                        style = MaterialTheme.typography.labelSmall,
                    )
                    Text(
                        "Radius: ${note.radius}m",
                        style = MaterialTheme.typography.labelSmall,
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Saved",
                            tint = Color(0xFF81C784), // Light Green
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Saved: ${
                                DateFormat.getDateTimeInstance().format(Date(note.createdAt))
                            }",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }

                    if (note.updateAt != 0L) {
                        Text(
                            "🛠️ Updated: ${
                                DateFormat.getDateTimeInstance()
                                    .format(Date(note.updateAt))
                            }",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    noteViewModel.isAddressSelected = true
                    navController.navigate(Screen.WriteNoteScreen.routeWithNoteId(note.id))
                    mapboxViewModel.clearSelectedNote()
                }) {
                    Text("Edit note")
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

//If device does not support OpenGL ES 3.0, mapbox map doesn't work. So checking openGL version here
fun isOpenGL3Supported(context: Context): Boolean {
    val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val configInfo = activityManager.deviceConfigurationInfo
    return configInfo.reqGlEsVersion >= 0x30000
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
