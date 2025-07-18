package com.example.nearbynote.nearbyNoteMainFunction.note.ui

import android.Manifest
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.nearbynote.R
import com.example.nearbynote.nearbyNoteMainFunction.geoFenceAPI.ui.GeofenceViewModel
import com.example.nearbynote.nearbyNoteMainFunction.note.data.NoteEntity
import com.example.nearbynote.nearbyNoteMainFunction.savedAddress.ui.SavedAddressViewModel
import com.example.nearbynote.nearbyNoteNav.Screen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import java.text.DateFormat
import java.util.Date

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NoteListMain(
    navController: NavController,
    noteViewModel: NoteViewModel,
    geofenceViewModel: GeofenceViewModel,
    modifier: Modifier = Modifier,
    savedAddressViewModel: SavedAddressViewModel
) {
    val notes by noteViewModel.notes.collectAsState()
    val allGeofences by geofenceViewModel.allGeofences.collectAsState(initial = emptyList())

    val savedAddresses by savedAddressViewModel.savedAddresses.collectAsState()

    var hasLaunchedPermissionRequest by rememberSaveable { mutableStateOf(false) }

    val notificationPermissionState = rememberPermissionState(
        Manifest.permission.POST_NOTIFICATIONS
    )
    val isNotificationPermissionRequired = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    var showDeleteDialog by remember { mutableStateOf(false) }
    var noteToDelete by remember { mutableStateOf<NoteEntity?>(null) }

    val tabs = listOf("With Location", "Without Location")
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

    val filteredNotes = when (selectedTabIndex) {
        0 -> notes.filter { it.geofenceId != null }  // With Location
        1 -> notes.filter { it.geofenceId == null } // Without Location
        else -> notes
    }


    LaunchedEffect(Unit) {
        if (!hasLaunchedPermissionRequest) {
            hasLaunchedPermissionRequest = true

            if (isNotificationPermissionRequired &&
                notificationPermissionState.status is PermissionStatus.Denied
            ) {
                notificationPermissionState.launchPermissionRequest()
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (notes.isEmpty()) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "There is no note! Create new one",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    navController.navigate(Screen.WriteNoteScreen.routeWithNoteId(null))
                }) {
                    Text("Create Note")
                }
            }
        } else {
            Column {
                TabRow(selectedTabIndex = selectedTabIndex) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(title) }
                        )
                    }
                }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        Text(
                            if (selectedTabIndex == 0)
                                "📝 Notes with Location" else "\uD83D\uDCDD Notes without Location",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    items(filteredNotes) { note ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clickable {
                                    noteViewModel.isAddressSelected = true
                                    navController.navigate(
                                        Screen.WriteNoteScreen.routeWithNoteId(
                                            note.id
                                        )
                                    )
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {

                                val addressName =
                                    savedAddresses.find { it.placeName == note.locationName }?.name
                                        ?: note.locationName

                                Text(
                                    text = note.content,
                                    maxLines = 3,
                                    overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                //Text(text = "Geofence ID: ${note.geofenceId}")

                                if (addressName != null) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (savedAddresses.any { it.placeName == note.locationName }) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.ic_favorite_addresses),
                                                contentDescription = "Favorite Address",
                                                modifier = Modifier.size(12.dp),
                                                tint = Color.Red
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                        }

                                        Text(
                                            text = addressName,
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                                Text(
                                    "✔\uFE0F Saved: ${
                                        DateFormat.getDateTimeInstance()
                                            .format(Date(note.createdAt))
                                    }",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Gray
                                )
                                if (note.updatedAt != 0L) {
                                    Text(
                                        "🛠️ Updated: ${
                                            DateFormat.getDateTimeInstance()
                                                .format(Date(note.updatedAt))
                                        }",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.Gray
                                    )
                                }
                            }

                            IconButton(onClick = {
                                noteToDelete = note
                                showDeleteDialog = true
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete Note")
                            }
                        }
                        HorizontalDivider()
                    }

                    /*            item {
                            Text(
                                "📍 Geofences",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(8.dp)
                            )
                        }

                        items(allGeofences) { geofence ->
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text("ID: ${geofence.id}")
                                Text("📍 ${geofence.addressName}")
                                Text("Lat: ${geofence.latitude}, Lng: ${geofence.longitude}")
                                Text("Radius: ${geofence.radius}m")
                                Text(
                                    "Created at: ${
                                        DateFormat.getDateTimeInstance().format(Date(geofence.createdAt))
                                    }"
                                )
                                HorizontalDivider()
                            }
                        }*/
                }
            }

            FloatingActionButton(
                onClick = {
                    navController.navigate(Screen.WriteNoteScreen.routeWithNoteId(null))
                },
                shape = CircleShape,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Note")
            }


            if (showDeleteDialog && noteToDelete != null) {
                AlertDialog(
                    onDismissRequest = {
                        showDeleteDialog = false
                        noteToDelete = null
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            noteToDelete?.let {
                                noteViewModel.deleteNoteAndGeofence(
                                    noteId = it.id,
                                    geofenceViewModel = geofenceViewModel
                                )
                            }
                            showDeleteDialog = false
                            noteToDelete = null
                        }) {
                            Text("Delete")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            showDeleteDialog = false
                            noteToDelete = null
                        }) {
                            Text("Cancel")
                        }
                    },
                    title = { Text("Delete note?") },
                    text = { Text("Are you sure you want to delete this note?") }
                )
            }
        }
    }
}
