package com.pjh.nearbynote.nearbyNoteMainFunction.geoFenceAPI.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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

/*
@Composable
fun BasicGeofenceSetup(
    geofenceViewModel: GeofenceViewModel,
    geofenceOptionsEnabled: Boolean = false,
    isFavoriteAddress: MutableState<Boolean>,
    favoriteAddressName: MutableState<String>,
    isFavoriteAddressDisable: MutableState<Boolean>,
    shouldDisableSavedAddressRow: Boolean,
    isGeofenceImmutable: Boolean
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

    val clickableModifier = if (isFavoriteAddressDisable.value || shouldDisableSavedAddressRow) {
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
                color = if (!isFavoriteAddressDisable.value && !shouldDisableSavedAddressRow) Color.Black else Color.Gray
            )
        }

        if (isFavoriteAddress.value) {
            OutlinedTextField(
                value = favoriteAddressName.value,
                onValueChange = { if (it.length <= 20) favoriteAddressName.value = it },
                label = { Text("Name for this address?") },
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

        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

        Text(
            text = "📏 Radius: $radiusDisplay",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Slider(
                value = radiusSliderValue.coerceIn(0f, 2000f),
                onValueChange = {
                    radiusSliderValue = it
                    geofenceViewModel.onRadiusChanged(it.toInt().toString())
                },
                valueRange = 100f..2000f,
                steps = 18,
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp),
                enabled = !isGeofenceImmutable
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
                enabled = !isGeofenceImmutable
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf(100, 300, 500, 700).forEach { preset ->
                OutlinedButton(
                    onClick = {
                        radiusSliderValue = preset.toFloat()
                        geofenceViewModel.onRadiusChanged(preset.toString())
                    },
                    shape = RoundedCornerShape(20),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    enabled = !isGeofenceImmutable
                ) {
                    Text("$preset m", style = MaterialTheme.typography.labelMedium)
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        */
/*        Text(
                    text = "Recommended radius is 300m-1000m for better accuracy.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))*//*


        */
/*        Button(
                    onClick = { geofenceViewModel.onRemoveAllGeofencesClick() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Remove All Geofences")
                }*//*

    }
}*/

@Composable
fun BasicGeofenceSetup(
    geofenceViewModel: GeofenceViewModel,
    geofenceOptionsEnabled: Boolean = false,
    isFavoriteAddress: MutableState<Boolean>,
    favoriteAddressName: MutableState<String>,
    isFavoriteAddressDisable: MutableState<Boolean>,
    shouldDisableSavedAddressRow: Boolean,
    isGeofenceImmutable: Boolean
) {
    val radius by geofenceViewModel.radius.collectAsState()
    var radiusSliderValue by remember { mutableFloatStateOf(radius.toFloatOrNull() ?: 300f) }

    val radiusDisplay = remember(radius) {
        val radiusFloat = radius.toFloatOrNull() ?: 0f
        if (radiusFloat >= 1000f) {
            "${"%.1f".format(radiusFloat / 1000)} km"
        } else {
            "${radiusFloat.toInt()} m"
        }
    }

    val isFavoriteInteractive = !isFavoriteAddressDisable.value && !shouldDisableSavedAddressRow

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {

        // --- Favorite section ---
        Text(
            text = "Favorite place",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Save this location as a reusable address so you can quickly pick it for other notes.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        val favoriteRowModifier = if (isFavoriteInteractive) {
            Modifier
                .fillMaxWidth()
                .clickable { isFavoriteAddress.value = !isFavoriteAddress.value }
        } else {
            Modifier.fillMaxWidth()
        }

        Row(
            modifier = favoriteRowModifier.padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isFavoriteAddress.value) {
                    Icons.Default.Favorite
                } else {
                    Icons.Default.FavoriteBorder
                },
                contentDescription = "Save as favorite",
                tint = if (isFavoriteAddress.value && isFavoriteInteractive) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Save this location as a favorite",
                color = if (isFavoriteInteractive) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                style = MaterialTheme.typography.bodyMedium
            )
        }

        if (isFavoriteAddress.value) {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = favoriteAddressName.value,
                onValueChange = {
                    if (it.length <= 20) favoriteAddressName.value = it
                },
                label = { Text("Favorite name") },
                placeholder = { Text("e.g. Home, Office") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                singleLine = true,
                enabled = isFavoriteInteractive
            )
            Text(
                text = "This name will show up in your saved places list.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 4.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        // --- Radius section ---
        Text(
            text = "Radius",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "How far from the center this note should be triggered.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Current radius: $radiusDisplay",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Slider(
                value = radiusSliderValue.coerceIn(0f, 2000f),
                onValueChange = {
                    radiusSliderValue = it
                    geofenceViewModel.onRadiusChanged(it.toInt().toString())
                },
                valueRange = 100f..2000f,
                steps = 18,
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp),
                enabled = !isGeofenceImmutable
            )

            Spacer(modifier = Modifier.width(12.dp))

            OutlinedTextField(
                value = radius,
                onValueChange = { text ->
                    val newValue = text.toFloatOrNull()
                    if (newValue != null) {
                        radiusSliderValue = newValue
                        geofenceViewModel.onRadiusChanged(newValue.toInt().toString())
                    }
                },
                label = { Text("Radius (m)") },
                singleLine = true,
                modifier = Modifier.width(100.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                enabled = !isGeofenceImmutable
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf(100, 300, 500, 700).forEach { preset ->
                val isSelected = radius.toFloatOrNull()?.toInt() == preset

                OutlinedButton(
                    onClick = {
                        radiusSliderValue = preset.toFloat()
                        geofenceViewModel.onRadiusChanged(preset.toString())
                    },
                    shape = RoundedCornerShape(20),
                    border = BorderStroke(
                        1.dp,
                        if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.outline
                        }
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    enabled = !isGeofenceImmutable,
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (isSelected) {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                        } else {
                            Color.Transparent
                        },
                        contentColor = if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                ) {
                    Text("$preset m", style = MaterialTheme.typography.labelMedium)
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Recommended range is usually between 300 m and 1 km for most locations.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
