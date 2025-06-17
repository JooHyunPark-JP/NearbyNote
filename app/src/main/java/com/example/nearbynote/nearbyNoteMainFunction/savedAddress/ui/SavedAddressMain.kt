package com.example.nearbynote.nearbyNoteMainFunction.savedAddress.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.nearbynote.nearbyNoteMainFunction.savedAddress.data.SavedAddressEntity
import com.example.nearbynote.nearbyNoteNav.Screen
import java.text.DateFormat
import java.util.Date

@Composable
fun SavedAddressMain(
    savedAddressViewModel: SavedAddressViewModel,
    navController: NavController
) {
    val savedAddresses by savedAddressViewModel.savedAddresses.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var addressToDelete by remember { mutableStateOf<SavedAddressEntity?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
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

            items(savedAddresses, key = { it.id }) { address ->
                Column(modifier = Modifier.padding(vertical = 8.dp)) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "🏷️ ${address.name}",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )

                        IconButton(onClick = {
                            addressToDelete = address
                            showDialog = true
                        }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Address",
                                tint = Color.Red
                            )
                        }
                    }

                    Text("📍 ${address.placeName}", style = MaterialTheme.typography.bodyMedium)
                    Text("🧭 Lat: ${address.latitude}, Lng: ${address.longitude}")
                    Text(
                        "📅 Saved: ${
                            DateFormat.getDateTimeInstance().format(Date(address.createdAt))
                        }",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )

                    HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
                }
            }
        }


        FloatingActionButton(
            onClick = { navController.navigate(Screen.SavedAddressAddScreen.route) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Saved Address")
        }

        if (showDialog && addressToDelete != null) {
            val address = addressToDelete
            AlertDialog(
                onDismissRequest = {
                    showDialog = false
                    addressToDelete = null
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (address != null) {
                            savedAddressViewModel.deleteAddress(address)
                        }
                        showDialog = false
                        addressToDelete = null
                    }) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showDialog = false
                        addressToDelete = null
                    }) {
                        Text("Cancel")
                    }
                },
                title = { Text("Delete address?") },
                text = { Text("Are you sure you want to delete '${addressToDelete?.name}'?") }
            )
        }
    }
}