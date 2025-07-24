package com.example.nearbynote.nearbyNoteMainFunction.note.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.nearbynote.nearbyNoteMainFunction.geoFenceAPI.ui.BasicGeofenceSetup
import com.example.nearbynote.nearbyNoteMainFunction.geoFenceAPI.ui.GeofenceViewModel
import com.example.nearbynote.nearbyNoteMainFunction.savedAddress.data.SavedAddressEntity
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        val suggestions = noteViewModel.suggestions
        val isAddressSearching = noteViewModel.isSearching


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
            Text(
                text = "📍 This note already has a location reminder. To change location, please create a new note.",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray),
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = !shouldDisableSavedAddressRow) {
                    onExpandedChange(true)
                }
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = if (!isSavedAddressClicked.value) Icons.Default.FavoriteBorder else Icons.Default.Favorite,
                contentDescription = "Select Favorite Address",
                tint = if (isSavedAddressClicked.value) Color.Red else Color.Gray
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = selectedAddress?.name ?: "Choose your favorite address",
                color = if (!shouldDisableSavedAddressRow) Color.Black else Color.Gray
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpandedChange(false) }
            ) {
                DropdownMenuItem(
                    text = { Text("No favorite address") },
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
                        text = { HorizontalDivider() },
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

        BasicGeofenceSetup(
            geofenceViewModel = geofenceViewModel,
            geofenceOptionsEnabled = geofenceEnabled,
            isFavoriteAddress = isFavoriteAddress,
            favoriteAddressName = favoriteAddressName,
            isFavoriteAddressDisable = isFavoriteAddressDisable,
            shouldDisableSavedAddressRow = shouldDisableSavedAddressRow,
            isGeofenceImmutable = isGeofenceImmutable
        )


        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally),
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


    }
}