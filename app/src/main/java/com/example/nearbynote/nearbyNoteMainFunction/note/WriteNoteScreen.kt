package com.example.nearbynote.nearbyNoteMainFunction.note

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun WriteNoteScreen(navController: NavController) {
    var noteText by remember { mutableStateOf("") }
    var geofenceEnabled by remember { mutableStateOf(false) }
    var geofenceText by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp)
        ) {
            GeofenceToggleRow(
                geofenceEnabled = geofenceEnabled,
                onToggle = { geofenceEnabled = it }
            )

            AnimatedVisibility(visible = geofenceEnabled) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = geofenceText,
                        onValueChange = { geofenceText = it },
                        placeholder = { Text("Enter location name or address") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            NoteTextField(
                noteText = noteText,
                onNoteChange = { noteText = it },
                modifier = Modifier.weight(1f)
            )
        }

        BottomFABRow(
            onVoiceClick = { /* TODO: Voice recognition */ },
            onSaveClick = { /* TODO: Save */ },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun GeofenceToggleRow(
    geofenceEnabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .padding(start = 16.dp)
                .size(24.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(
                    if (geofenceEnabled)
                        MaterialTheme.colorScheme.primary
                    else
                        Color.Gray
                )
        ) {
            IconToggleButton(
                checked = geofenceEnabled,
                onCheckedChange = onToggle,
                modifier = Modifier.fillMaxSize()
            ) {
                if (geofenceEnabled) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Enabled",
                        tint = Color.White
                    )
                }
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text("Enable location alert", fontSize = 16.sp)
    }
}

@Composable
fun NoteTextField(
    noteText: String,
    onNoteChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = noteText,
        onValueChange = onNoteChange,
        placeholder = { Text("노트를 입력하세요...") },
        textStyle = LocalTextStyle.current.copy(lineHeight = 24.sp),
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun BottomFABRow(
    onVoiceClick: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(bottom = 24.dp, start = 24.dp, end = 24.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        //mic (voice regonition)
        FloatingActionButton(onClick = onVoiceClick, modifier = Modifier.size(56.dp)) {
            Icon(Icons.Default.Phone, contentDescription = "Voice Input")
        }

        Spacer(modifier = Modifier.weight(1f))

        FloatingActionButton(onClick = onSaveClick, modifier = Modifier.size(56.dp)) {
            Icon(Icons.Default.Phone, contentDescription = "Save")
        }
    }
}