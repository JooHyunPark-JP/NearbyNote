package com.pjh.nearbynote.nearbyNoteMainFunction.note.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pjh.nearbynote.nearbyNoteMainFunction.geoFenceAPI.ui.GeofenceViewModel
import com.pjh.nearbynote.nearbyNoteMainFunction.mapBoxAPI.data.AddressSuggestion


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

        OutlinedTextField(
            value = addressQuery,
            onValueChange = {
                noteViewModel.isAddressSelected = false
                wasSuggestionManuallyCleared = false
                onQueryChange(it)
                geofenceViewModel.onLatitudeChanged("")
                geofenceViewModel.onLongitudeChanged("")
            },
            placeholder = { Text("Search for a place") },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = null,
                    tint = if (enabled) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            },
            trailingIcon = {
                when {
                    isAddressSearching -> {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                    }

                    addressQuery.isNotEmpty() && enabled -> {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear address",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .size(18.dp)
                                .clickable {
                                    noteViewModel.isAddressSelected = false
                                    wasSuggestionManuallyCleared = true
                                    onQueryChange("")
                                    geofenceViewModel.onLatitudeChanged("")
                                    geofenceViewModel.onLongitudeChanged("")
                                }
                        )
                    }
                }
            }
        )

        if (!isAddressSearching &&
            suggestions.isEmpty() &&
            addressQuery.length >= 4 &&
            !isSavedAddressClicked &&
            !wasSuggestionManuallyCleared &&
            !noteViewModel.isAddressSelected
        ) {
            Text(
                text = "No results. Check the address or your network and try again.",
                modifier = Modifier
                    .padding(top = 6.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = Color.Gray,
                fontSize = 12.sp
            )
        }

        if (suggestions.isNotEmpty()) {
            Spacer(modifier = Modifier.heightIn(min = 4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 220.dp)
                    .padding(top = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    )
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    itemsIndexed(
                        suggestions,
                        key = { _, item -> item.placeName }) { index, suggestion ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    noteViewModel.isAddressSelected = true
                                    wasSuggestionManuallyCleared = true
                                    onSuggestionSelected(suggestion)
                                }
                                .padding(horizontal = 12.dp, vertical = 10.dp)
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
                                    text = suggestion.placeName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        if (index < suggestions.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 12.dp),
                                color = MaterialTheme.colorScheme.outlineVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

