package com.example.nearbynote.nearbyNoteMainFunction.note.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.nearbynote.R
import com.example.nearbynote.nearbyNoteMainFunction.note.data.NoteEntity
import com.example.nearbynote.nearbyNoteMainFunction.savedAddress.ui.SavedAddressViewModel
import java.text.DateFormat
import java.util.Date

@Composable
fun ReadNoteScreen(
    navController: NavController,
    noteViewModel: NoteViewModel,
    savedAddressViewModel: SavedAddressViewModel,
    noteId: Long?
) {
    var note by remember { mutableStateOf<NoteEntity?>(null) }

    //val notesAtSameLocation by noteViewModel.getNotesByLocationName(note.locationName).collectAsState(emptyList())
    val scrollState = rememberScrollState()

    val savedAddresses by savedAddressViewModel.savedAddresses.collectAsState()

    var sameLocationNotes by remember { mutableStateOf<List<NoteEntity>>(emptyList()) }

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    LaunchedEffect(noteId) {
        if (noteId != null) {
            val loadedNote = noteViewModel.getNoteById(noteId)
            note = loadedNote

            // get the other note on this location
            loadedNote?.locationName?.let { location ->
                noteViewModel.getNotesByLocationName(location)
                    .collect { notes ->
                        sameLocationNotes = notes.filter { it.id != loadedNote.id }
                    }
            }
        }
    }

    note?.let { note ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                val addressName =
                    savedAddresses.find { it.placeName == note.locationName }?.name
                        ?: note.locationName

                Box(modifier = Modifier.fillMaxWidth()) {
                    Image(
                        painter = painterResource(id = R.drawable.note_pin2),
                        contentDescription = "Pin",
                        modifier = Modifier
                            .size(32.dp)
                            .align(Alignment.TopCenter)
                            .zIndex(1f)
                    )
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                            .height(screenHeight * 0.4f)
                            .shadow(
                                elevation = 10.dp,
                                shape = RoundedCornerShape(20.dp),
                                ambientColor = Color.Black.copy(alpha = 0.08f),
                                spotColor = Color.Black.copy(alpha = 1.00f)
                            ),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                            ) {
                                val innerScrollState = rememberScrollState()
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                        .verticalScroll(innerScrollState)
                                ) {
                                    Text(
                                        text = note.content,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Column {
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
                                        if (addressName != null) {
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
                            }
                        }
                    }
                }
            }

            if (sameLocationNotes.isNotEmpty()) {
                item {
                    Text(
                        "Other Notes at Same Location:",
                        style = MaterialTheme.typography.titleSmall
                    )
                }
                items(sameLocationNotes) { it ->
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Image(
                            painter = painterResource(id = R.drawable.note_pin2),
                            contentDescription = "Pin",
                            modifier = Modifier
                                .size(32.dp)
                                .align(Alignment.TopCenter)
                                .zIndex(1f)
                        )
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp)
                                .shadow(
                                    elevation = 10.dp,
                                    shape = RoundedCornerShape(20.dp),
                                    ambientColor = Color.Black.copy(alpha = 0.08f),
                                    spotColor = Color.Black.copy(alpha = 1.00f)
                                ),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(it.content, style = MaterialTheme.typography.bodyMedium)
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    "✔\uFE0F ${
                                        DateFormat.getDateTimeInstance().format(Date(it.createdAt))
                                    }",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Gray
                                )
                                if (it.updatedAt != 0L) {
                                    Text(
                                        "🛠️ Updated: ${
                                            DateFormat.getDateTimeInstance()
                                                .format(Date(it.updatedAt))
                                        }",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

