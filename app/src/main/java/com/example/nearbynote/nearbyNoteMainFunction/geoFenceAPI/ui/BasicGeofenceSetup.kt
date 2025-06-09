package com.example.nearbynote.nearbyNoteMainFunction.geoFenceAPI.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Text("Set Radius for this note to trigger!", style = MaterialTheme.typography.titleMedium)
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
                modifier = Modifier.weight(1f)
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
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
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

        Button(
            onClick = { geofenceViewModel.onRemoveAllGeofencesClick() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Remove All Geofences")
        }

        Spacer(modifier = Modifier.height(12.dp))

        /*        Text(
                    text = geofenceStatus,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth()
                )*/
    }
}