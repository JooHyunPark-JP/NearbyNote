package com.pjh.nearbynote.nearbyNoteMainFunction.savedAddress.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.pjh.nearbynote.nearbyNoteMainFunction.geoFenceAPI.ui.GeofenceViewModel
import com.pjh.nearbynote.nearbyNoteMainFunction.mapBoxAPI.data.AddressSuggestion
import com.pjh.nearbynote.nearbyNoteMainFunction.note.ui.AddressSearchSection
import com.pjh.nearbynote.nearbyNoteMainFunction.note.ui.NoteViewModel


@Composable
fun SavedAddressAdd(
    navController: NavController,
    savedAddressViewModel: SavedAddressViewModel,
    noteViewModel: NoteViewModel,
    geofenceViewModel: GeofenceViewModel
) {
    val context = LocalContext.current
    val addressQuery = noteViewModel.addressQuery
    val suggestions = noteViewModel.suggestions
    val isAddressSearching = noteViewModel.isSearching

    val favoriteName = remember { mutableStateOf("") }
    val selectedSuggestion = remember { mutableStateOf<AddressSuggestion?>(null) }

    // Reset search state when this screen is opened
    LaunchedEffect(Unit) {
        noteViewModel.addressQuery = ""
        noteViewModel.suggestions = emptyList()
        favoriteName.value = ""
        selectedSuggestion.value = null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Header
        Text(
            text = "Save a favorite place",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Pick a location from the search below and give it a short name so you can reuse it when creating notes.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Location section
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
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
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Location",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Search for a place and select one from the suggestions.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                AddressSearchSection(
                    addressQuery = addressQuery,
                    onQueryChange = {
                        noteViewModel.onQueryChanged(it)
                        selectedSuggestion.value = null
                    },
                    suggestions = suggestions,
                    onSuggestionSelected = { suggestion ->
                        selectedSuggestion.value = suggestion
                        noteViewModel.addressQuery = suggestion.placeName
                        noteViewModel.suggestions = emptyList()
                    },
                    isAddressSearching = isAddressSearching,
                    noteViewModel = noteViewModel,
                    geofenceViewModel = geofenceViewModel
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Name section
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
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
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Create,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Name this place",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Use a short label you’ll recognize later, like “Home”, “Office”, or “Gym”.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = favoriteName.value,
                    onValueChange = {
                        if (it.length <= 20) favoriteName.value = it
                    },
                    label = { Text("Name for this place") },
                    placeholder = { Text("e.g. Home, Office") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        }

        // Push button to the bottom
        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                val suggestion = selectedSuggestion.value
                val name = favoriteName.value.trim()
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

                if (suggestion == null) {
                    Toast.makeText(
                        context,
                        "Please select a place from the suggestions before saving.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@Button
                }

                if (name.isBlank()) {
                    Toast.makeText(
                        context,
                        "Please enter a name for this place.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@Button
                }

                if (
                    savedAddressViewModel.isDuplicateAddress(
                        suggestion.placeName,
                        suggestion.latitude,
                        suggestion.longitude
                    )
                ) {
                    Toast.makeText(
                        context,
                        "This place is already saved in your favorites.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@Button
                }

                savedAddressViewModel.saveAddress(
                    name = name,
                    placeName = suggestion.placeName,
                    lat = suggestion.latitude,
                    lng = suggestion.longitude
                )

                Toast.makeText(
                    context,
                    "Place saved to your favorites.",
                    Toast.LENGTH_SHORT
                ).show()

                favoriteName.value = ""
                selectedSuggestion.value = null
                noteViewModel.onQueryChanged("")
                navController.popBackStack()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 4.dp)
        ) {
            Text("Save place")
        }
    }
}