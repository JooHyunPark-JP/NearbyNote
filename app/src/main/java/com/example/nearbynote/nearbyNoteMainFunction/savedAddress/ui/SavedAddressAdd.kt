package com.example.nearbynote.nearbyNoteMainFunction.savedAddress.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.nearbynote.nearbyNoteMainFunction.geoFenceAPI.ui.GeofenceViewModel
import com.example.nearbynote.nearbyNoteMainFunction.mapBoxAPI.data.AddressSuggestion
import com.example.nearbynote.nearbyNoteMainFunction.note.ui.AddressSearchSection
import com.example.nearbynote.nearbyNoteMainFunction.note.ui.NoteViewModel

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

    val favoriteName = remember { mutableStateOf("") }
    val selectedSuggestion = remember { mutableStateOf<AddressSuggestion?>(null) }

    val isAddressSearching = noteViewModel.isSearching

    LaunchedEffect(Unit) {
        noteViewModel.addressQuery = ""
        noteViewModel.suggestions = emptyList()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("⭐ Add Favorite Address", style = MaterialTheme.typography.titleMedium)

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

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = favoriteName.value,
            onValueChange = { if (it.length <= 20) favoriteName.value = it },
            label = { Text("Name for this address") },
            placeholder = { Text("e.g. Home, School") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val suggestion = selectedSuggestion.value
                val name = favoriteName.value.trim()
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

                if (suggestion == null) {
                    Toast.makeText(context, "Please select a valid address from the suggestions list.", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                if (name.isBlank()) {
                    Toast.makeText(context, "Please enter a name of this address.", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                if (savedAddressViewModel.isDuplicateAddress(
                        suggestion.placeName,
                        suggestion.latitude,
                        suggestion.longitude
                    )
                ) {
                    Toast.makeText(
                        context,
                        "This address already exists in your favorites.",
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
                Toast.makeText(context, "Address saved!", Toast.LENGTH_SHORT).show()

                favoriteName.value = ""
                selectedSuggestion.value = null
                noteViewModel.onQueryChanged("")
                navController.popBackStack()


            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Save Address")
        }
    }
}