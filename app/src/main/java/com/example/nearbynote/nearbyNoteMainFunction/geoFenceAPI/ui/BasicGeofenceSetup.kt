package com.example.nearbynote.nearbyNoteMainFunction.geoFenceAPI.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import java.util.UUID

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun BasicGeofenceSetup(
    geofenceViewModel: GeofenceViewModel,
    geofenceManager: GeofenceManager
) {
    val context = LocalContext.current
    val latitude by geofenceViewModel.latitude.collectAsState()
    val longitude by geofenceViewModel.longitude.collectAsState()
    val radius by geofenceViewModel.radius.collectAsState()
    val address by geofenceViewModel.address.collectAsState()
    val geofenceStatus by geofenceViewModel.geofenceMessage.collectAsState()

    val fineLocationPermission = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    var backgroundLocationGranted by remember { mutableStateOf(false) }
    var showBackgroundDialog by remember { mutableStateOf(false) }

    val settingsLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            backgroundLocationGranted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        }

    val geofenceId = "nearbyNote_" + UUID.randomUUID().toString()
    Log.d("GeofenceSetup", "Creating geofence with ID: $geofenceId")

    LaunchedEffect(Unit) {
        backgroundLocationGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Geofence Setup", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = latitude,
            onValueChange = { geofenceViewModel.onLatitudeChanged(it) },
            label = { Text("Latitude") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = longitude,
            onValueChange = { geofenceViewModel.onLongitudeChanged(it) },
            label = { Text("Longitude") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = radius,
            onValueChange = { geofenceViewModel.onRadiusChanged(it) },
            label = { Text("Radius (meters)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Address: $address",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = { geofenceViewModel.onFetchAddressClick() },
                modifier = Modifier.weight(1f)
            ) {
                Text("Fetch Address")
            }

            Button(
                onClick = {
                    if (!fineLocationPermission.status.isGranted) {
                        when {
                            fineLocationPermission.status.shouldShowRationale -> {
                                fineLocationPermission.launchPermissionRequest()
                            }

                            else -> {
                                showBackgroundDialog = true
                            }
                        }
                        return@Button
                    }

                    if (!backgroundLocationGranted) {
                        showBackgroundDialog = true
                        return@Button
                    }

                    val lat = latitude.toDoubleOrNull()
                    val lng = longitude.toDoubleOrNull()
                    val rad = radius.toFloatOrNull()

                    if (lat != null && lng != null && rad != null) {
                        geofenceManager.addGeofence(
                            geofenceId = geofenceId,
                            latitude = lat,
                            longitude = lng,
                            radius = rad,
                            onSuccess = {
                                geofenceViewModel.updateGeofenceStatus("Geofence added successfully")
                            },
                            onFailure = {
                                geofenceViewModel.updateGeofenceStatus("Failed to add geofence: ${it.message}")
                            }
                        )
                    } else {
                        geofenceViewModel.updateGeofenceStatus("Invalid input")
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Add Geofence")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { geofenceViewModel.onRemoveAllGeofencesClick() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Remove All Geofences")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = geofenceStatus,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.fillMaxWidth()
        )
    }

    if (showBackgroundDialog) {
        AlertDialog(
            onDismissRequest = { showBackgroundDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    showBackgroundDialog = false
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    settingsLauncher.launch(intent)
                }) {
                    Text("Open Settings")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showBackgroundDialog = false
                }) {
                    Text("Cancel")
                }
            },
            title = { Text("Background location permission Required") },
            text = {
                Text(
                    buildString {
                        appendLine("📍 For a smarter note-taking experience...")
                        appendLine()
                        appendLine("To keep showing your note when you arrive at a location, your phone requires you to enable \"Allow all the time\" manually.")
                        appendLine()
                        appendLine("📌 Don’t worry — your location is never stored or shared.")
                        appendLine("You can always change anytime in Settings.")
                        appendLine()
                        appendLine("🔧 How to set it:")
                        appendLine("1. Tap \"Open Settings\" below")
                        appendLine("2. Tap \"Permissions\"")
                        appendLine("3. Choose \"Location\"")
                        appendLine("4. Select \"Allow all the time\"")
                    }
                )
            }
        )
    }
}