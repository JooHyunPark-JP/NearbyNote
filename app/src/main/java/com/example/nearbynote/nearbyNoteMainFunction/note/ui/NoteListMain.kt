package com.example.nearbynote.nearbyNoteMainFunction.note.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.nearbynote.nearbyNoteMainFunction.geoFenceAPI.ui.GeofenceViewModel
import com.example.nearbynote.nearbyNoteNav.Screen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.shouldShowRationale
import java.text.DateFormat
import java.util.Date

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NoteListMain(
    navController: NavController,
    noteViewModel: NoteViewModel,
    geofenceViewModel: GeofenceViewModel,
    modifier: Modifier = Modifier
) {

    val notes by noteViewModel.notes.collectAsState()
    val scrollState = rememberScrollState()
    val allGeofences by geofenceViewModel.allGeofences.collectAsState(initial = emptyList())


    val locationPermissionsState = rememberMultiplePermissionsState(
        listOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )
    //   val context = LocalContext.current
    //   var showPermissionDialog by remember { mutableStateOf(false) }

    var hasLaunchedPermissionRequest by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val allGranted = locationPermissionsState.allPermissionsGranted
        val shouldShow = locationPermissionsState.permissions.any { it.status.shouldShowRationale }

        /*        val allDeniedWithoutRationale = locationPermissionsState.permissions.all {
                    it.status is PermissionStatus.Denied && !it.status.shouldShowRationale
                }*/

        when {
            allGranted -> { /* OK */
            }

            !hasLaunchedPermissionRequest -> {
                hasLaunchedPermissionRequest = true
                locationPermissionsState.launchMultiplePermissionRequest()
            }

            shouldShow -> locationPermissionsState.launchMultiplePermissionRequest()
            // allDeniedWithoutRationale -> showPermissionDialog = true
        }
    }


    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyColumn {
                items(notes) { note ->
                    Text(text = note.content, modifier = Modifier.padding(4.dp))
                    Text(text = note.geofenceId.toString())
                }
            }

            LazyColumn {
                items(items = allGeofences) { geofence ->
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        Text("ID: ${geofence.id}")
                        Text("📍 ${geofence.name}")
                        Text("Lat: ${geofence.latitude}, Lng: ${geofence.longitude}")
                        Text("Radius: ${geofence.radius}m")
                        Text(
                            "Created at: ${
                                DateFormat.getDateTimeInstance().format(Date(geofence.createdAt))
                            }"
                        )
                        HorizontalDivider()
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = {
                navController.navigate(Screen.WriteNoteScreen.route)
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Note")
        }
    }

    /*    if (showPermissionDialog) {
            AlertDialog(
                onDismissRequest = { showPermissionDialog = false },
                confirmButton = {
                    TextButton(onClick = {
                        showPermissionDialog = false
                        context.startActivity(
                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", context.packageName, null)
                            }
                        )
                    }) {
                        Text("Open Setting")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showPermissionDialog = false
                    }) {
                        Text("Cancel")
                    }
                },
                title = { Text("Location Permission required") },
                text = {
                    Text("Location access required in order to use Geofence setup. Please grant permission in settings.")
                }
            )
        }*/
}