package com.example.nearbynote.nearbyNoteMainFunction.note.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nearbynote.R
import com.example.nearbynote.nearbyNoteMainFunction.geoFenceAPI.ui.BasicGeofenceSetup
import com.example.nearbynote.nearbyNoteMainFunction.geoFenceAPI.ui.GeofenceManager
import com.example.nearbynote.nearbyNoteMainFunction.geoFenceAPI.ui.GeofenceViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun WriteNoteScreen(
    navController: NavController,
    noteViewModel: NoteViewModel,
    geofenceViewModel: GeofenceViewModel,
    geofenceManager: GeofenceManager
) {
    val context = LocalContext.current
    val permissionState = rememberPermissionState(android.Manifest.permission.RECORD_AUDIO)
    var showPermissionDialog by remember { mutableStateOf(false) }
    var hasRequestedPermission by rememberSaveable { mutableStateOf(false) }


    var noteText by remember { mutableStateOf("") }
    var geofenceEnabled by remember { mutableStateOf(false) }
    var geofenceText by remember { mutableStateOf("") }

    val suggestions = noteViewModel.suggestions

    //Launcher for speech recognition
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val results = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            results?.firstOrNull()?.let { recognized ->
                noteText = listOf(noteText, recognized).filter { it.isNotBlank() }.joinToString(" ")
            }
        }
    }

    fun startVoiceRecognition() {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            Toast.makeText(
                context,
                "Speech recognition unavailable. Requires Google app & Play Services.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        //Current setting is only for 'English'
        val intent = createVoiceRecognitionIntent("en-US")
        speechLauncher.launch(intent)

    }


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
                        value = noteViewModel.addressQuery,
                        onValueChange = { noteViewModel.onQueryChanged(it) },
                        placeholder = { Text("Enter location name or address") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            LazyColumn {
                items(
                    items = suggestions,
                    key = { it.placeName }
                ) { suggestion ->
                    Text(
                        text = "${suggestion.placeName} (Lat: ${suggestion.latitude}, Lon: ${suggestion.longitude})",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                geofenceViewModel.onSuggestionSelected(suggestion)
                                noteViewModel.addressQuery = suggestion.placeName
                                noteViewModel.suggestions = emptyList()
                            }
                            .padding(8.dp)
                    )
                }
            }

            if (geofenceEnabled) {
                Spacer(modifier = Modifier.height(16.dp))
                BasicGeofenceSetup(
                    geofenceViewModel = geofenceViewModel,
                    geofenceManager = geofenceManager
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            NoteTextField(
                noteText = noteText,
                onNoteChange = { noteText = it },
                modifier = Modifier.weight(1f)
            )
        }

        BottomFABRow(
            onVoiceClick = {
                when (val status = permissionState.status) {
                    is PermissionStatus.Granted -> {
                        startVoiceRecognition()
                    }

                    is PermissionStatus.Denied -> {
                        if (!hasRequestedPermission) {
                            hasRequestedPermission = true
                            permissionState.launchPermissionRequest()
                        } else if (status.shouldShowRationale) {
                            permissionState.launchPermissionRequest()
                        } else {
                            showPermissionDialog = true
                        }
                    }
                }
            },
            onSaveClick = {
                if (noteText.isBlank()) {
                    Toast.makeText(
                        context,
                        "Write down something before saving!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    noteViewModel.saveNote(
                        content = noteText,
                        geofenceId = if (geofenceEnabled) geofenceText else null,
                        locationName = geofenceText,
                        isVoice = false
                    )
                    navController.popBackStack()
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        if (showPermissionDialog) {
            AlertDialog(
                onDismissRequest = { showPermissionDialog = false },
                confirmButton = {
                    TextButton(onClick = {
                        showPermissionDialog = false
                        context.startActivity(
                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", context.packageName, null)
                            }
                        )
                    }) {
                        Text("Open App Setting")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showPermissionDialog = false
                    }) {
                        Text("Cancel")
                    }
                },
                title = { Text("Permission Required") },
                text = {
                    Text(
                        "Voice recognition requires microphone access.\n" +
                                "Please grant permission in settings."
                    )
                }
            )
        }
    }
}

fun createVoiceRecognitionIntent(language: String): Intent {
    return Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, language)
        putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...")
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
        placeholder = { Text("Write down anything!") },
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
            Icon(painterResource(R.drawable.ic_microphone), contentDescription = "Voice Input")
        }

        Spacer(modifier = Modifier.weight(1f))

        FloatingActionButton(onClick = onSaveClick, modifier = Modifier.size(56.dp)) {
            Icon(Icons.Default.Add, contentDescription = "Save")
        }
    }
}


