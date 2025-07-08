package com.example.nearbynote.nearbyNoteMainFunction.permissionStatus.ui

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionStatusScreen(navController: NavController) {
    val context = LocalContext.current

    val backgroundLocationPermission =
        rememberPermissionState(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
    val fineLocationPermission = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val notificationPermission = rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
    val microphonePermission = rememberPermissionState(Manifest.permission.RECORD_AUDIO)

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
            status = backgroundLocationPermission.status,
            onInfoClick = {})
        PermissionRow(
            label = "Fine Location",
            status = fineLocationPermission.status,
            onInfoClick = {})
        PermissionRow(
            label = "Notifications",
            status = notificationPermission.status,
            onInfoClick = {})
        PermissionRow(
            label = "Microphone",
            status = microphonePermission.status,
            onInfoClick = {})

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
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionRow(
    label: String,
    status: PermissionStatus,
    onInfoClick: () -> Unit
) {
    val icon = when (status) {
        is PermissionStatus.Granted -> "✔️"
        is PermissionStatus.Denied -> "❌"
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
                onClick = onInfoClick,
                modifier = Modifier.size(20.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Permission Info",
                    tint = Color.Gray
                )
            }
        }
        Text(icon)
    }
}