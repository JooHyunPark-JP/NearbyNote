package com.pjh.nearbynote.nearbyNoteMainFunction.note.ui

import android.Manifest
import android.os.Build
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.pjh.nearbynote.R
import com.pjh.nearbynote.nearbyNoteMainFunction.geoFenceAPI.ui.GeofenceViewModel
import com.pjh.nearbynote.nearbyNoteMainFunction.note.data.NoteEntity
import com.pjh.nearbynote.nearbyNoteMainFunction.savedAddress.ui.SavedAddressViewModel
import com.pjh.nearbynote.nearbyNoteNav.Screen
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
    val savedAddresses by savedAddressViewModel.savedAddresses.collectAsState()

    var hasLaunchedPermissionRequest by rememberSaveable { mutableStateOf(false) }

    val notificationPermissionState = rememberPermissionState(
        Manifest.permission.POST_NOTIFICATIONS
    )
    val isNotificationPermissionRequired = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    var showDeleteDialog by remember { mutableStateOf(false) }
    var noteToDelete by remember { mutableStateOf<NoteEntity?>(null) }

    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

    val notesWithLocation = notes.filter { it.geofenceId != null }
    val notesWithoutLocation = notes.filter { it.geofenceId == null }

    val tabs = listOf(
        "All Notes" to notes.size,
        "With Location" to notesWithLocation.size,
        "No Location" to notesWithoutLocation.size
    )

    val filteredNotes = when (selectedTabIndex) {
        0 -> notes
        1 -> notesWithLocation
        2 -> notesWithoutLocation
        else -> notes
    }

    val pinImages = listOf(
        R.drawable.note_pin_red,
        R.drawable.note_pin_green,
        R.drawable.note_pin_lightpurple,
        R.drawable.note_pin_green,
        R.drawable.note_pin_skyblue,
        R.drawable.note_pin_yellow
    )

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
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.note_pin_skyblue),
                    contentDescription = "Note pin",
                    modifier = Modifier.size(72.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No notes yet",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Create your first note and optionally link it to a place so we can remind you when you're nearby.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = {
                        navController.navigate(Screen.WriteNoteScreen.routeWithNoteId(null))
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Create note")
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                TabRow(selectedTabIndex = selectedTabIndex) {
                    tabs.forEachIndexed { index, (title, count) ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                ) {
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.bodySmall,
                                        maxLines = 1
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "($count)",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (filteredNotes.isEmpty()) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = when (selectedTabIndex) {
                                        1 -> "No notes with a location yet."
                                        2 -> "No notes without a location yet."
                                        else -> "No notes in this view yet."
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        items(filteredNotes) { note ->

                            val pinForNote = remember(note.id) {
                                pinImages[(note.id % pinImages.size).toInt()]
                            }

                            val hasLocation = note.geofenceId != null
                            val addressName =
                                savedAddresses.find { it.placeName == note.locationName }?.name
                                    ?: note.locationName

                            Box(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Image(
                                    painter = painterResource(id = pinForNote),
                                    contentDescription = "Pin",
                                    modifier = Modifier
                                        .size(28.dp)
                                        .align(Alignment.TopCenter)
                                        .zIndex(1f)
                                )

                                Card(
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp, vertical = 12.dp)
                                        .fillMaxWidth()
                                        .clickable {
                                            noteViewModel.isAddressSelected = true
                                            navController.navigate(
                                                Screen.WriteNoteScreen.routeWithNoteId(note.id)
                                            )
                                        },
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surface
                                    ),
                                    shape = RoundedCornerShape(20.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 14.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .weight(1f)
                                        ) {
                                            Text(
                                                text = note.content,
                                                maxLines = 3,
                                                overflow = TextOverflow.Ellipsis,
                                                style = MaterialTheme.typography.bodyLarge
                                            )

                                            Spacer(modifier = Modifier.height(12.dp))

                                            // Address row (only if there is a location)
                                            if (addressName != null && hasLocation) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    if (savedAddresses.any { it.placeName == note.locationName }) {
                                                        Icon(
                                                            painter = painterResource(id = R.drawable.ic_favorite_addresses),
                                                            contentDescription = "Favorite address",
                                                            modifier = Modifier.size(14.dp),
                                                            tint = MaterialTheme.colorScheme.primary
                                                        )
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                    }
                                                    Text(
                                                        text = addressName,
                                                        style = MaterialTheme.typography.labelMedium,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                }
                                            }

                                            Row(
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Created",
                                                    tint = Color(0xFF81C784),
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(2.dp))
                                                Text(
                                                    text = "Created: ${
                                                        DateFormat.getDateTimeInstance()
                                                            .format(Date(note.createdAt))
                                                    }",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }

                                            Row(
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {

                                                if (note.updatedAt != 0L) {
                                                    Icon(
                                                        imageVector = Icons.Default.Build,
                                                        contentDescription = "Created",
                                                        tint = Color(0xFF81C784),
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(2.dp))
                                                    Text(
                                                        text = "Updated: ${
                                                            DateFormat.getDateTimeInstance()
                                                                .format(Date(note.updatedAt))
                                                        }",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }
                                        }

                                        IconButton(
                                            onClick = {
                                                noteToDelete = note
                                                showDeleteDialog = true
                                            },
                                            modifier = Modifier.align(Alignment.CenterVertically)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete note"
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
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
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add note")
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

