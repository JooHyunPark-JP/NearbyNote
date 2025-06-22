package com.example.nearbynote.nearbyNoteMainFunction.geoFenceAPI.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun BasicGeofenceSetup(
    geofenceViewModel: GeofenceViewModel,
    geofenceOptionsEnabled: Boolean = false,
    isFavoriteAddress: MutableState<Boolean>,
    favoriteAddressName: MutableState<String>,
    isFavoriteAddressDisable: Boolean,
    shouldDisableSavedAddressRow: Boolean
) {
    val radius by geofenceViewModel.radius.collectAsState()
    //   val geofenceStatus by geofenceViewModel.geofenceMessage.collectAsState()
    var radiusSliderValue by remember { mutableFloatStateOf(radius.toFloatOrNull() ?: 300f) }

    val radiusDisplay = remember(radius) {
        val radiusFloat = radius.toFloatOrNull() ?: 0f
        if (radiusFloat >= 1000f) {
            "${"%.1f".format(radiusFloat / 1000)} km"
        } else {
            "${radiusFloat.toInt()} m"
        }
    }

    val clickableModifier = if (isFavoriteAddressDisable || shouldDisableSavedAddressRow) {
        Modifier
    } else {
        Modifier.clickable { isFavoriteAddress.value = !isFavoriteAddress.value }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(clickableModifier)
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isFavoriteAddress.value) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "Add to Favorites",
                tint = if (isFavoriteAddress.value) Color.Red else Color.Gray
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Add to favorite addresses",
                color = if (!isFavoriteAddressDisable && !shouldDisableSavedAddressRow) Color.Black else Color.Gray
            )
        }

        if (isFavoriteAddress.value) {
            OutlinedTextField(
                value = favoriteAddressName.value,
                onValueChange = { if (it.length <= 20) favoriteAddressName.value = it },
                label = { Text("Name for this favorite address?") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                singleLine = true,
                placeholder = {
                    Text(
                        text = "e.g. Home, School",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                    )
                }
            )
        }

        Text(
            text = "📏 Radius: $radiusDisplay",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .padding(top = 4.dp)
                .align(Alignment.CenterHorizontally)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Slider(
                value = radiusSliderValue.coerceIn(100f, 2000f),
                onValueChange = {
                    radiusSliderValue = it
                    geofenceViewModel.onRadiusChanged(it.toInt().toString())
                },
                valueRange = 100f..2000f,
                steps = 18,
                modifier = Modifier.weight(1f),
                enabled = geofenceOptionsEnabled
            )

            Spacer(modifier = Modifier.width(12.dp))

            OutlinedTextField(
                value = radius,
                onValueChange = {
                    val newValue = it.toFloatOrNull()
                    if (newValue != null) {
                        radiusSliderValue = newValue
                        geofenceViewModel.onRadiusChanged(newValue.toInt().toString())
                    }
                },
                label = { Text("Radius(m)") },
                singleLine = true,
                modifier = Modifier.width(100.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                enabled = geofenceOptionsEnabled
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Recommended radius is 300m-1000m for better accuracy.",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        /*        Button(
                    onClick = { geofenceViewModel.onRemoveAllGeofencesClick() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Remove All Geofences")
                }*/
    }
}