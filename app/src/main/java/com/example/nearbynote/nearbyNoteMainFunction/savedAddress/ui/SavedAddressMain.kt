package com.example.nearbynote.nearbyNoteMainFunction.savedAddress.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.text.DateFormat
import java.util.Date

@Composable
fun SavedAddressMain(savedAddressViewModel: SavedAddressViewModel) {
    val savedAddresses by savedAddressViewModel.savedAddresses.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text(
                "⭐ Favourite Addresses",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        items(savedAddresses) { address ->
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Text("🏷️ ${address.name}", style = MaterialTheme.typography.bodyLarge)
                Text("📍 ${address.placeName}", style = MaterialTheme.typography.bodyMedium)
                Text("🧭 Lat: ${address.latitude}, Lng: ${address.longitude}")
                Text(
                    "📅 Saved: ${DateFormat.getDateTimeInstance().format(Date(address.createdAt))}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
                HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
            }
        }
    }
}