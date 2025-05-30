package com.example.nearbynote.nearbyNoteMainFunction.geoFenceAPI.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BasicGeofenceSetup(
    geofenceViewModel: GeofenceViewModel
) {
    val latitude by geofenceViewModel.latitude.collectAsState()
    val longitude by geofenceViewModel.longitude.collectAsState()
    val radius by geofenceViewModel.radius.collectAsState()
    val address by geofenceViewModel.address.collectAsState()
    val geofenceStatus by geofenceViewModel.geofenceMessage.collectAsState()

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
                onClick = { geofenceViewModel.onAddGeofenceClick() },
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
}