package com.pjh.nearbynote.nearbyNoteMainFunction.permissionStatus.ui

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.pjh.nearbynote.BuildConfig
import com.pjh.nearbynote.nearbyNoteDemo.ui.ReviewDemoSection
import com.pjh.nearbynote.nearbyNoteDemo.ui.triggerGeofenceDemo
import com.pjh.nearbynote.nearbyNoteMainFunction.note.ui.NoteViewModel


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionStatusScreen(
    navController: NavController,
    noteViewModel: NoteViewModel
) {
    val context = LocalContext.current

    val backgroundLocationPermission =
        rememberPermissionState(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
    val fineLocationPermission = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val notificationPermission = rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
    val microphonePermission = rememberPermissionState(Manifest.permission.RECORD_AUDIO)

    // val allGeofences by geofenceViewModel.allGeofences.collectAsState(initial = emptyList())

    val latestNoteId by noteViewModel.latestGeofencedNoteId.collectAsState(initial = null)

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            "To make this app fully functional, please allow the following permissions.",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(16.dp))

        Text("Permission List", style = MaterialTheme.typography.titleMedium)

        Spacer(Modifier.height(8.dp))

        PermissionRow(
            label = "Background Location",
            description = buildString {
                appendLine("Needed to trigger location alerts when the app is in the background.")
                appendLine()
                appendLine("ℹ️ Android requires users to enable background location manually.")
                appendLine()
                appendLine("How to enable:")
                appendLine("1. Tap \"Open Settings\" below")
                appendLine("2. Tap \"Permissions\"")
                appendLine("3. Select \"Location\"")
                appendLine("4. Choose \"Allow all the time\" (this includes both Fine + background location)")
            },
            status = backgroundLocationPermission.status
        )

        PermissionRow(
            label = "Fine Location",
            description = buildString {
                appendLine("Provides precise location for geofencing and centering the map on your location.")
                appendLine()
                appendLine("How to enable:")
                appendLine("1. Tap \"Open Settings\" below")
                appendLine("2. Tap \"Permissions\"")
                appendLine("3. Select \"Location\"")
                appendLine("4. Choose \"Allow all the time\" or \"While using the app\"")
            },
            status = fineLocationPermission.status
        )

        PermissionRow(
            label = "Notifications",
            description = buildString {
                appendLine("Sends alerts when you arrive at saved locations.")
                appendLine()
                appendLine("How to enable:")
                appendLine("1. Tap \"Open Settings\" below")
                appendLine("2. Tap \"Notifications\"")
                appendLine("3. Turn notifications on")
            },
            status = notificationPermission.status
        )

        PermissionRow(
            label = "Microphone",
            description = buildString {
                appendLine("Allows voice-to-text when creating notes.")
                appendLine()
                appendLine("How to enable:")
                appendLine("1. Tap \"Open Settings\" below")
                appendLine("2. Tap \"Permissions\"")
                appendLine("3. Select \"Microphone\"")
                appendLine("4. Choose \"Allow only while using the app\"")
            },
            status = microphonePermission.status
        )

        Button(
            onClick = {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                }
                context.startActivity(intent)
            },
            modifier = Modifier.padding(top = 24.dp)
        ) {
            Text("Go to Settings")
        }

        Spacer(modifier = Modifier.height(8.dp))

        //Demo section for google tester and closed testing testers
        if (BuildConfig.REVIEW_MODE) {
            ReviewDemoSection(
                latestNoteId = latestNoteId,
                onTrigger = { id ->
                    if (id != null) {
                        triggerGeofenceDemo(context, id)
                    } else {
                        Toast.makeText(
                            context,
                            "Please create a geofenced note (Note with location) first to trigger events.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            )
        }

        /*        //For testing purposes
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    item {
                        Text(
                            "📍 Geofences",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    items(allGeofences) { geofence ->
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text("ID: ${geofence.id}")
                            Text("📍 ${geofence.addressName}")
                            Text("Lat: ${geofence.latitude}, Lng: ${geofence.longitude}")
                            Text("Radius: ${geofence.radius}m")
                            Text(
                                "Created at: ${
                                    DateFormat.getDateTimeInstance()
                                        .format(Date(geofence.createdAt))
                                }"
                            )
                            HorizontalDivider()
                        }
                    }
                }*/
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionRow(
    label: String,
    description: String,
    status: PermissionStatus
) {
    var showDialog by remember { mutableStateOf(false) }


    val iconImage: ImageVector
    val iconColor: Color

    when (status) {
        is PermissionStatus.Granted -> {
            iconImage = Icons.Default.Check
            iconColor = Color(0xFF81C784)
        }

        is PermissionStatus.Denied -> {
            iconImage = Icons.Default.Close
            iconColor = Color(0xFFFF2C2C)
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) { Text("OK") }
            },
            title = { Text(label) },
            text = { Text(description) }
        )
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Text(label)

            Spacer(modifier = Modifier.width(4.dp))

            IconButton(
                onClick = { showDialog = true },
                modifier = Modifier.size(20.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Permission Info",
                    tint = Color.Gray
                )
            }
        }
        Icon(
            imageVector = iconImage,
            contentDescription = null,
            tint = iconColor,
        )
    }
}

