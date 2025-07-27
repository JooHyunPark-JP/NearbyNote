package com.example.nearbynote.nearbyNoteMainFunction.note.ui

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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
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


    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

    val notesWithLocation = notes.filter { it.geofenceId != null }
    val notesWithoutLocation = notes.filter { it.geofenceId == null }

    val filteredNotes = when (selectedTabIndex) {
        0 -> notesWithLocation
        1 -> notesWithoutLocation
        else -> notes
    }

    val tabs = listOf(
        "With Location (${notesWithLocation.size})",
        "Without Location (${notesWithoutLocation.size})"
    )

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

                        val pinForNote = remember(note.id) {
                            pinImages[(note.id % pinImages.size).toInt()]
                        }

                        Box(modifier = Modifier.fillMaxWidth()) {
                            Image(
                                painter = painterResource(id = pinForNote),
                                contentDescription = "Pin",
                                modifier = Modifier
                                    .size(28.dp)
                                    .align(Alignment.TopCenter)
                                    //.offset(y = (8).dp)
                                    .zIndex(1f)

                            )

                            Card(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth()
                                    .shadow(
                                        elevation = 10.dp,
                                        shape = RoundedCornerShape(20.dp),
                                        ambientColor = Color.Black.copy(alpha = 0.08f),
                                        spotColor = Color.Black.copy(alpha = 1.00f)
                                    )
                                    .clickable {
                                        noteViewModel.isAddressSelected = true
                                        navController.navigate(
                                            Screen.WriteNoteScreen.routeWithNoteId(note.id)
                                        )
                                    },
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                                shape = RoundedCornerShape(20.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                            )
                            {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                    ) {
                                        val addressName =
                                            savedAddresses.find { it.placeName == note.locationName }?.name
                                                ?: note.locationName

                                        Text(
                                            text = note.content,
                                            maxLines = 3,
                                            overflow = TextOverflow.Ellipsis,
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontStyle = FontStyle.Italic
                                        )

                                        Spacer(modifier = Modifier.height(12.dp))

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

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Saved",
                                                tint = Color(0xFF81C784),
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "Saved: ${
                                                    DateFormat.getDateTimeInstance()
                                                        .format(Date(note.createdAt))
                                                }",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color.Gray
                                            )
                                        }

                                        if (note.updatedAt != 0L) {
                                            Text(
                                                text = "🛠️ Updated: ${
                                                    DateFormat.getDateTimeInstance()
                                                        .format(Date(note.updatedAt))
                                                }",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color.Gray
                                            )
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
                                            Icons.Default.Delete,
                                            contentDescription = "Delete Note"
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


