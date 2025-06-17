package com.example.nearbynote.nearbyNoteMainFunction.note.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.nearbynote.nearbyNoteMainFunction.mapBoxAPI.data.AddressSuggestion

@Composable
fun AddressSearchSection(
    addressQuery: String,
    onQueryChange: (String) -> Unit,
    suggestions: List<AddressSuggestion>,
    onSuggestionSelected: (AddressSuggestion) -> Unit,
    enabled: Boolean = true
) {
    Column {
        TextField(
            value = addressQuery,
            onValueChange = onQueryChange,
            placeholder = { Text("Enter location name or address") },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 200.dp)
                .padding(top = 4.dp)
        ) {
            items(suggestions, key = { it.placeName }) { suggestion ->
                Text(
                    text = "${suggestion.placeName} (Lat: ${suggestion.latitude}, Lon: ${suggestion.longitude})",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSuggestionSelected(suggestion) }
                        .padding(8.dp)
                )
            }
        }
    }
}