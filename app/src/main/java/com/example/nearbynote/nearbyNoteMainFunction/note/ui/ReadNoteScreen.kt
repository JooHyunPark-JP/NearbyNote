package com.example.nearbynote.nearbyNoteMainFunction.note.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
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
    val scrollState = rememberScrollState()

    val savedAddresses by savedAddressViewModel.savedAddresses.collectAsState()

    LaunchedEffect(noteId) {
        if (noteId != null) {
            note = noteViewModel.getNoteById(noteId)
        }
    }

    note?.let { note ->
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            val addressName =
                savedAddresses.find { it.placeName == note.locationName }?.name
                    ?: note.locationName

            Text(note.content, style = MaterialTheme.typography.bodyLarge)

            Spacer(Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(Modifier.height(8.dp))
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
                "✔\uFE0F ${DateFormat.getDateTimeInstance().format(Date(note.createdAt))}",
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
    }
}
