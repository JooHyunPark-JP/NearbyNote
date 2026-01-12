package com.pjh.nearbynote.nearbyNoteMainFunction.note.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pjh.nearbynote.nearbyNoteMainFunction.geoFenceAPI.ui.BasicGeofenceSetup
import com.pjh.nearbynote.nearbyNoteMainFunction.geoFenceAPI.ui.GeofenceViewModel
import com.pjh.nearbynote.nearbyNoteMainFunction.savedAddress.data.SavedAddressEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeofenceSheetContent(
    isGeofenceImmutable: Boolean,
    isSavedAddressClicked: MutableState<Boolean>,
    noteViewModel: NoteViewModel,
    geofenceViewModel: GeofenceViewModel,
    savedAddresses: List<SavedAddressEntity>,
    selectedAddress: SavedAddressEntity?,
    onSelectAddress: (SavedAddressEntity?) -> Unit,
    isFavoriteAddress: MutableState<Boolean>,
    isFavoriteAddressDisable: MutableState<Boolean>,
    favoriteAddressName: MutableState<String>,
    shouldDisableSavedAddressRow: Boolean,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    coroutineScope: CoroutineScope,
    sheetState: SheetState,
    showGeofenceSheet: MutableState<Boolean>,
    geofenceEnabled: Boolean
) {
    val suggestions = noteViewModel.suggestions
    val isAddressSearching = noteViewModel.isSearching

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Sheet header
        Text(
            text = "Location reminder",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = CardDefaults.shape,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Text(
                    text = "Location",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Search for a place you want this note to be triggered at.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                AddressSearchSection(
                    addressQuery = noteViewModel.addressQuery,
                    onQueryChange = { noteViewModel.onQueryChanged(it) },
                    suggestions = suggestions,
                    onSuggestionSelected = { suggestion ->
                        geofenceViewModel.onSuggestionSelected(suggestion)
                        noteViewModel.addressQuery = suggestion.placeName
                        noteViewModel.addressLatitude = suggestion.latitude
                        noteViewModel.addressLongitude = suggestion.longitude
                        noteViewModel.suggestions = emptyList()
                    },
                    enabled = !isGeofenceImmutable && !isSavedAddressClicked.value,
                    isAddressSearching = isAddressSearching,
                    isSavedAddressClicked = isSavedAddressClicked.value,
                    noteViewModel = noteViewModel,
                    geofenceViewModel = geofenceViewModel
                )

                if (isGeofenceImmutable) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "📍 This note already has a location reminder. To change the location, please create a new note.",
                        textAlign = TextAlign.Start,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = CardDefaults.shape,
            colors = CardDefaults.cardColors(
                containerColor = if (shouldDisableSavedAddressRow) {
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                } else {
                    MaterialTheme.colorScheme.surface
                }
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Text(
                    text = "Saved places",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )


                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Quickly reuse a place you saved earlier, like “Home” or “Office”.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                Box {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = !shouldDisableSavedAddressRow) {
                                if (!shouldDisableSavedAddressRow) {
                                    onExpandedChange(true)
                                }
                            }
                            .padding(horizontal = 8.dp, vertical = 10.dp)
                    ) {
                        Icon(
                            imageVector = if (!isSavedAddressClicked.value) {
                                Icons.Default.FavoriteBorder
                            } else {
                                Icons.Default.Favorite
                            },
                            contentDescription = "Select saved place",
                            tint = if (isSavedAddressClicked.value) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = selectedAddress?.name ?: "Choose a saved place",
                            color = if (!shouldDisableSavedAddressRow) {
                                MaterialTheme.colorScheme.onSurface
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = if (!shouldDisableSavedAddressRow) {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            }
                        )
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { onExpandedChange(false) }
                    ) {
                        DropdownMenuItem(
                            text = { Text("No saved place") },
                            onClick = {
                                onSelectAddress(null)
                                onExpandedChange(false)
                                noteViewModel.addressQuery = ""
                                noteViewModel.addressLatitude = 0.0
                                noteViewModel.addressLongitude = 0.0
                                geofenceViewModel.onLatitudeChanged("")
                                geofenceViewModel.onLongitudeChanged("")
                                isFavoriteAddressDisable.value = false
                                isSavedAddressClicked.value = false
                                isFavoriteAddress.value = false
                                favoriteAddressName.value = ""
                                noteViewModel.suggestions = emptyList()
                            }
                        )

                        if (savedAddresses.isNotEmpty()) {
                            DropdownMenuItem(
                                text = {
                                    HorizontalDivider(
                                        color = MaterialTheme.colorScheme.outlineVariant
                                    )
                                },
                                onClick = {},
                                enabled = false
                            )

                            Column(
                                modifier = Modifier
                                    .heightIn(max = 300.dp)
                                    .verticalScroll(rememberScrollState())
                            ) {
                                savedAddresses.forEach { address ->
                                    DropdownMenuItem(
                                        text = { Text(address.name) },
                                        onClick = {
                                            onSelectAddress(address)
                                            onExpandedChange(false)
                                            noteViewModel.addressQuery = address.placeName
                                            noteViewModel.addressLatitude = address.latitude
                                            noteViewModel.addressLongitude = address.longitude
                                            geofenceViewModel.onLatitudeChanged(address.latitude.toString())
                                            geofenceViewModel.onLongitudeChanged(address.longitude.toString())
                                            isFavoriteAddressDisable.value = true
                                            isSavedAddressClicked.value = true
                                            isFavoriteAddress.value = false
                                            favoriteAddressName.value = ""
                                            noteViewModel.suggestions = emptyList()
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = CardDefaults.shape,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                BasicGeofenceSetup(
                    geofenceViewModel = geofenceViewModel,
                    geofenceOptionsEnabled = geofenceEnabled,
                    isFavoriteAddress = isFavoriteAddress,
                    favoriteAddressName = favoriteAddressName,
                    isFavoriteAddressDisable = isFavoriteAddressDisable,
                    shouldDisableSavedAddressRow = shouldDisableSavedAddressRow,
                    isGeofenceImmutable = isGeofenceImmutable
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally),
            onClick = {
                coroutineScope.launch {
                    sheetState.hide()
                }.invokeOnCompletion {
                    showGeofenceSheet.value = false
                }
            }
        ) {
            Text("Done")
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}
