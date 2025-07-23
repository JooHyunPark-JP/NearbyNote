package com.example.nearbynote.nearbyNoteMainFunction.note.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nearbynote.nearbyNoteMainFunction.geoFenceAPI.ui.GeofenceViewModel
import com.example.nearbynote.nearbyNoteMainFunction.mapBoxAPI.data.AddressSuggestion

@Composable
fun AddressSearchSection(
    addressQuery: String,
    onQueryChange: (String) -> Unit,
    suggestions: List<AddressSuggestion>,
    onSuggestionSelected: (AddressSuggestion) -> Unit,
    enabled: Boolean = true,
    isAddressSearching: Boolean,
    isSavedAddressClicked: Boolean = false,
    noteViewModel: NoteViewModel,
    geofenceViewModel: GeofenceViewModel
) {
    var wasSuggestionManuallyCleared by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(top = 4.dp)) {
        TextField(
            value = addressQuery,
            onValueChange = {
                noteViewModel.isAddressSelected = false
                wasSuggestionManuallyCleared = false
                onQueryChange(it)
                geofenceViewModel.onLatitudeChanged("")
                geofenceViewModel.onLongitudeChanged("")
            },
            placeholder = { Text("Enter the address") },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled
        )

        if (isAddressSearching) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, top = 4.dp)
            ) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Searching address...")
            }
        }

        if (!isAddressSearching &&
            suggestions.isEmpty() &&
            addressQuery.length >= 4 &&
            !isSavedAddressClicked &&
            !wasSuggestionManuallyCleared &&
            !noteViewModel.isAddressSelected

        ) {
            Text(
                "📭 No address or check your internet connection!",
                modifier = Modifier
                    .padding(top = 4.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = Color.Gray,
                fontSize = 12.sp
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 200.dp)
                .padding(top = 4.dp)
        ) {
            items(suggestions, key = { it.placeName }) { suggestion ->
                Text(
                    //text = "${suggestion.placeName} (Lat: ${suggestion.latitude}, Lon: ${suggestion.longitude})",
                    text = suggestion.placeName,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            noteViewModel.isAddressSelected = true
                            wasSuggestionManuallyCleared = true
                            onSuggestionSelected(suggestion)
                        }
                        .padding(8.dp)
                )
            }
        }
    }
}