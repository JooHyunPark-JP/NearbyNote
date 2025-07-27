package com.example.nearbynote.nearbyNoteMainFunction.note.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.nearbynote.R
import com.example.nearbynote.nearbyNoteMainFunction.geoFenceAPI.data.GeofenceEntity
import com.example.nearbynote.nearbyNoteMainFunction.geoFenceAPI.ui.GeofenceManager
import com.example.nearbynote.nearbyNoteMainFunction.geoFenceAPI.ui.GeofenceViewModel
import com.example.nearbynote.nearbyNoteMainFunction.savedAddress.data.SavedAddressEntity
import com.example.nearbynote.nearbyNoteMainFunction.savedAddress.ui.SavedAddressViewModel
import com.example.nearbynote.nearbyNoteNav.Screen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun WriteNoteScreen(
    navController: NavController,
    noteViewModel: NoteViewModel,
    geofenceViewModel: GeofenceViewModel,
    geofenceManager: GeofenceManager,
    savedAddressViewModel: SavedAddressViewModel,
    noteId: Long?
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    var showPermissionDialog by remember { mutableStateOf(false) }
    var hasRequestedPermission by rememberSaveable { mutableStateOf(false) }

    var noteText by remember { mutableStateOf("") }
    var geofenceEnabled by remember { mutableStateOf(false) }

    val googleVoicePermissionState = rememberPermissionState(Manifest.permission.RECORD_AUDIO)

    val backgroundLocationPermission =
        rememberPermissionState(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
    val notificationPermission = rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)

    val isNotificationPermissionRequired = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    var showBackgroundDialog by remember { mutableStateOf(false) }
    var showNotificationPermissionDialog by remember { mutableStateOf(false) }

    var hasExistingGeofence by remember { mutableStateOf(false) }

    val isGeofenceImmutable by remember(noteId, hasExistingGeofence) {
        mutableStateOf(noteId != null && hasExistingGeofence)
    }


    var isFavoriteAddress = remember { mutableStateOf(false) }
    var isFavoriteAddressDisable = remember { mutableStateOf(false) }
    val favoriteAddressName = remember { mutableStateOf("") }


    val savedAddresses by savedAddressViewModel.savedAddresses.collectAsState()
    var expanded by remember { mutableStateOf(false) }


    var selectedAddress by remember { mutableStateOf<SavedAddressEntity?>(null) }
    val isSavedAddressClicked = remember { mutableStateOf(false) }

    val shouldDisableSavedAddressRow by remember(noteId, hasExistingGeofence) {
        mutableStateOf(noteId != null && hasExistingGeofence)
    }

    // val shouldDisableSavedAddressRow = noteId != null && hasExistingGeofence


    val sheetState = rememberModalBottomSheetState(
        confirmValueChange = { false },// Disable swipe to dismiss
        skipPartiallyExpanded = true //expand bottomsheet to full screen.
    )
    val coroutineScope = rememberCoroutineScope()

    val showGeofenceSheet = rememberSaveable { mutableStateOf(false) }


    /*    val savedRowModifier = if (shouldDisableSavedAddressRow) {
            Modifier
        } else {
            Modifier.clickable { expanded = true }
        }*/

    fun resetNewNoteState() {
        noteText = ""

        //if user is coming from map
        if (noteViewModel.preserveMapLocation) {
            geofenceEnabled = true
            coroutineScope.launch {
                sheetState.show()
                showGeofenceSheet.value = true
            }

        } else {
            geofenceEnabled = false
            noteViewModel.addressQuery = ""
            geofenceViewModel.onLatitudeChanged("")
            geofenceViewModel.onLongitudeChanged("")
            geofenceViewModel.onRadiusChanged("1000")
        }

        noteViewModel.preserveMapLocation = false
    }

    suspend fun loadExistingNote(noteId: Long) {
        val existing = noteViewModel.getNoteById(noteId) ?: return
        noteText = existing.content
        geofenceEnabled = existing.geofenceId != null
        hasExistingGeofence = existing.geofenceId != null
        noteViewModel.addressQuery = existing.locationName.orEmpty()


        val geofence = existing.geofenceId?.let { geofenceViewModel.getGeofenceById(it) }
        if (geofence != null) {
            geofenceViewModel.onLatitudeChanged(geofence.latitude.toString())
            geofenceViewModel.onLongitudeChanged(geofence.longitude.toString())
            geofenceViewModel.onRadiusChanged(geofence.radius.toInt().toString())
        } else {
            noteViewModel.addressQuery = ""
            geofenceViewModel.onLatitudeChanged("")
            geofenceViewModel.onLongitudeChanged("")
            geofenceViewModel.onRadiusChanged("1000")
        }
    }

    LaunchedEffect(noteId) {
        if (noteId != null) {
            launch { loadExistingNote(noteId) }
        } else {
            resetNewNoteState()
        }
    }

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


    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            GeofenceToggleSwitch(
                enabled = geofenceEnabled,
                isGeofenceImmutable = !isGeofenceImmutable,
                onToggle = { enabled ->
                    geofenceEnabled = enabled
                    if (enabled) {
                        coroutineScope.launch {
                            sheetState.show()
                            showGeofenceSheet.value = true
                        }
                    }
                },
            )

            if (isGeofenceImmutable) {
                Text(
                    text = "📍 This note already has a location reminder. To change location, please create a new note.",
                    style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray),
                    textAlign = TextAlign.Center,
                )
            }

            if (geofenceEnabled) {
                OutlinedButton(
                    onClick = {
                        coroutineScope.launch {
                            sheetState.show()
                            showGeofenceSheet.value = true
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(horizontal = 8.dp)
                ) {
                    Text("Edit Location")
                }
            }

            if (showGeofenceSheet.value) {
                ModalBottomSheet(
                    onDismissRequest = {
                        coroutineScope.launch {
                            sheetState.hide()
                        }.invokeOnCompletion {
                            showGeofenceSheet.value = false
                        }
                    },
                    sheetState = sheetState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(
                            min = 1000.dp,
                            max = LocalConfiguration.current.screenHeightDp.dp * 0.95f
                        )
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .background(MaterialTheme.colorScheme.inversePrimary)
                        ) {
                            IconButton(
                                onClick = {
                                    coroutineScope.launch { sheetState.hide() }
                                        .invokeOnCompletion { showGeofenceSheet.value = false }
                                },
                                modifier = Modifier.align(Alignment.CenterEnd)
                            ) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = "Done",
                                )
                            }

                            Text(
                                "Set location for this Note",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }


                        GeofenceSheetContent(
                            isGeofenceImmutable = isGeofenceImmutable,
                            isSavedAddressClicked = isSavedAddressClicked,
                            noteViewModel = noteViewModel,
                            geofenceViewModel = geofenceViewModel,
                            savedAddresses = savedAddresses,
                            selectedAddress = selectedAddress,
                            onSelectAddress = { selectedAddress = it },
                            isFavoriteAddress = isFavoriteAddress,
                            isFavoriteAddressDisable = isFavoriteAddressDisable,
                            favoriteAddressName = favoriteAddressName,
                            shouldDisableSavedAddressRow = shouldDisableSavedAddressRow,
                            expanded = expanded,
                            onExpandedChange = { expanded = it },
                            coroutineScope = coroutineScope,
                            sheetState = sheetState,
                            showGeofenceSheet = showGeofenceSheet,
                            geofenceEnabled = geofenceEnabled
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))

            NoteTextField(
                noteText = noteText,
                onNoteChange = { noteText = it },
                modifier = Modifier.weight(1f),
            )
        }

        BottomFABRow(
            onVoiceClick = {
                when (val status = googleVoicePermissionState.status) {
                    is PermissionStatus.Granted -> {
                        startVoiceRecognition()
                    }

                    is PermissionStatus.Denied -> {
                        if (!hasRequestedPermission) {
                            hasRequestedPermission = true
                            googleVoicePermissionState.launchPermissionRequest()
                        } else if (status.shouldShowRationale) {
                            googleVoicePermissionState.launchPermissionRequest()
                        } else {
                            showPermissionDialog = true
                        }
                    }
                }
            },
            onSaveClick = {
                if (noteId == null) {
                    //create new note
                    handleNewNoteSave(
                        context = context,
                        noteText = noteText,
                        geofenceEnabled = geofenceEnabled,
                        backgroundLocationPermission = backgroundLocationPermission,
                        notificationPermission = notificationPermission,
                        isNotificationPermissionRequired = isNotificationPermissionRequired,
                        addressQuery = noteViewModel.addressQuery,
                        lat = geofenceViewModel.latitude.value.toDoubleOrNull(),
                        lng = geofenceViewModel.longitude.value.toDoubleOrNull(),
                        rad = geofenceViewModel.radius.value.toFloatOrNull(),
                        geofenceManager = geofenceManager,
                        geofenceViewModel = geofenceViewModel,
                        noteViewModel = noteViewModel,
                        navController = navController,
                        savedAddressViewModel = savedAddressViewModel,
                        isFavoriteAddress = isFavoriteAddress,
                        favoriteAddressName = favoriteAddressName,
                        onShowBackgroundDialog = { showBackgroundDialog = true },
                        onShowNotificationDialog = { showNotificationPermissionDialog = true }
                    )
                } else {
                    //Edit existed note
                    handleExistingNoteUpdate(
                        context = context,
                        noteId = noteId,
                        noteText = noteText,
                        geofenceEnabled = geofenceEnabled,
                        addressQuery = noteViewModel.addressQuery,
                        lat = geofenceViewModel.latitude.value.toDoubleOrNull(),
                        lng = geofenceViewModel.longitude.value.toDoubleOrNull(),
                        rad = geofenceViewModel.radius.value.toFloatOrNull(),
                        geofenceManager = geofenceManager,
                        geofenceViewModel = geofenceViewModel,
                        noteViewModel = noteViewModel,
                        navController = navController
                    )
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )


        //Pop up the background permission message to users
        if (showBackgroundDialog && geofenceEnabled) {
            AlertDialog(
                onDismissRequest = { showBackgroundDialog = false },
                confirmButton = {
                    TextButton(onClick = {
                        showBackgroundDialog = false
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        // settingsLauncher.launch(intent)
                        context.startActivity(intent)
                    }) {
                        Text("Open Settings")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showBackgroundDialog = false
                    }) {
                        Text("Cancel")
                    }
                },
                title = { Text("Background location permission Required") },
                text = {
                    Text(
                        buildString {
                            appendLine("📍 For a smarter note-taking experience...")
                            appendLine()
                            appendLine("To keep showing your note when you arrive at a location, your phone requires you to enable \"Allow all the time\" manually.")
                            appendLine()
                            appendLine("📌 Don’t worry — your location is never stored or shared.")
                            appendLine("You can always change anytime in Settings.")
                            appendLine()
                            appendLine("🔧 How to set:")
                            appendLine("1. Tap \"Open Settings\" below")
                            appendLine("2. Tap \"Permissions\"")
                            appendLine("3. Choose \"Location\"")
                            appendLine("4. Select \"Allow all the time\"")
                        }
                    )
                }
            )
        }

        //Pop up the background permission message to users
        if (showNotificationPermissionDialog && geofenceEnabled) {
            AlertDialog(
                onDismissRequest = { showNotificationPermissionDialog = false },
                confirmButton = {
                    TextButton(onClick = {
                        showNotificationPermissionDialog = false
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        //    settingsLauncher.launch(intent)
                        context.startActivity(intent)
                    }) {
                        Text("Open Settings")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showNotificationPermissionDialog = false
                    }) {
                        Text("Cancel")
                    }
                },
                title = { Text("Notification permission required") },
                text = {
                    Text(
                        buildString {
                            appendLine("To receive note notifications from your app, you need to enable notification permissions.")
                            appendLine()
                            appendLine("You can always change anytime in Settings.")
                            appendLine()
                            appendLine("🔧 How to set:")
                            appendLine("1. Tap \"Open Settings\" below")
                            appendLine("2. Tap \"Notifications\"")
                            appendLine("3. And turn the notification on")
                        }
                    )
                }
            )
        }

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

@OptIn(ExperimentalPermissionsApi::class)
fun handleNewNoteSave(
    context: Context,
    noteText: String,
    geofenceEnabled: Boolean,
    backgroundLocationPermission: PermissionState,
    notificationPermission: PermissionState,
    isNotificationPermissionRequired: Boolean,
    addressQuery: String,
    lat: Double?,
    lng: Double?,
    rad: Float?,
    geofenceManager: GeofenceManager,
    geofenceViewModel: GeofenceViewModel,
    noteViewModel: NoteViewModel,
    navController: NavController,
    savedAddressViewModel: SavedAddressViewModel,
    isFavoriteAddress: MutableState<Boolean>,
    favoriteAddressName: MutableState<String>,
    onShowBackgroundDialog: () -> Unit,
    onShowNotificationDialog: () -> Unit
) {
    if (noteText.isBlank()) {
        Toast.makeText(context, "Write down something before saving!", Toast.LENGTH_SHORT).show()
        return
    }

    /*    if (geofenceEnabled && !backgroundLocationGranted) {
            onShowBackgroundDialog()
            return
        }


        if (geofenceEnabled && !notificationPermissionGranted && isNotificationPermissionRequired) {
            onShowNotificationDialog()
            return
        }*/


    if (geofenceEnabled && backgroundLocationPermission.status !is PermissionStatus.Granted) {
        onShowBackgroundDialog()
        return
    }

    if (geofenceEnabled && notificationPermission.status !is PermissionStatus.Granted && isNotificationPermissionRequired) {
        onShowNotificationDialog()
        return
    }


    if (geofenceEnabled) {
        if (lat == null || lng == null || rad == null || addressQuery.isBlank()) {
            Toast.makeText(
                context,
                "Please choose an address from the list that appears as you type.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val geofenceId = UUID.randomUUID().toString()

        geofenceManager.addGeofence(
            geofenceId = geofenceId,
            latitude = lat,
            longitude = lng,
            radius = rad,
            onSuccess = {
                geofenceViewModel.saveGeofenceToDb(
                    id = geofenceId,
                    name = addressQuery,
                    lat = lat,
                    lng = lng,
                    radius = rad
                )
                noteViewModel.saveNote(
                    content = noteText,
                    geofenceId = geofenceId,
                    locationName = addressQuery,
                    isVoice = false,
                )

                if (isFavoriteAddress.value) {
                    savedAddressViewModel.saveAddress(
                        name = favoriteAddressName.value
                            .ifBlank { "Unnamed" }
                            .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                        placeName = addressQuery,
                        lat = lat,
                        lng = lng
                    )
                }

                noteViewModel.addressQuery = ""
                navController.navigate(Screen.Main.route)
            },
            onFailure = {
                Toast.makeText(
                    context,
                    "Failed to add geofence: ${it.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    } else {
        noteViewModel.saveNote(
            content = noteText,
            geofenceId = null,
            locationName = "Location hasn't been set",
            isVoice = false
        )
        navController.popBackStack()
    }
}

fun handleExistingNoteUpdate(
    context: Context,
    noteId: Long,
    noteText: String,
    geofenceEnabled: Boolean,
    addressQuery: String,
    lat: Double?,
    lng: Double?,
    rad: Float?,
    geofenceManager: GeofenceManager,
    geofenceViewModel: GeofenceViewModel,
    noteViewModel: NoteViewModel,
    navController: NavController
) {
    if (noteText.isBlank()) {
        Toast.makeText(context, "Write down something before saving!", Toast.LENGTH_SHORT).show()
        return
    }

    val geofenceEntity: GeofenceEntity? = if (geofenceEnabled) {
        if (lat == null || lng == null || rad == null) {
            Toast.makeText(context, "Invalid geofence data", Toast.LENGTH_SHORT).show()
            return
        }

        GeofenceEntity(
            id = "",
            addressName = addressQuery,
            latitude = lat,
            longitude = lng,
            radius = rad,
            createdAt = System.currentTimeMillis()
        )
    } else null

    noteViewModel.updateNoteWithGeofence(
        noteId = noteId,
        content = noteText,
        geofenceEntity = geofenceEntity,
        geofenceManager = geofenceManager,
        geofenceViewModel = geofenceViewModel,
        updatedAt = System.currentTimeMillis(),
        onSuccess = {
            noteViewModel.addressQuery = ""
            navController.navigate(Screen.Main.route)
        },
        onFailure = {
            Toast.makeText(
                context,
                it.message ?: "Failed to update note",
                Toast.LENGTH_LONG
            ).show()
        }
    )
}

@Composable
fun GeofenceToggleSwitch(
    enabled: Boolean,
    isGeofenceImmutable: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text("Location-based Reminder", style = MaterialTheme.typography.titleSmall)
            Text(
                if (enabled) "Alert will trigger when arriving at saved location"
                else "Enable to set location reminder",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
        Switch(
            checked = enabled,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.inversePrimary,
                uncheckedThumbColor = Color.LightGray
            ),
            enabled = isGeofenceImmutable
        )
    }
}

/*@Composable
fun NoteTextField(
    noteText: String,
    onNoteChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    TextField(
        value = noteText,
        onValueChange = onNoteChange,
        placeholder = { Text("Write your note here.") },
        textStyle = LocalTextStyle.current.copy(lineHeight = 24.sp),
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 120.dp, max = Dp.Infinity)
    )
}*/

@Composable
fun NoteTextField(
    noteText: String,
    onNoteChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val paperColor = Color(0xFFFFFDE7) // 연노랑 종이 느낌

    TextField(
        value = noteText,
        onValueChange = onNoteChange,
        placeholder = {
            Text(
                "Write your note here...",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontStyle = FontStyle.Italic,
                    color = Color.Gray
                )
            )
        },
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            fontStyle = FontStyle.Italic
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(4.dp, shape = RoundedCornerShape(12.dp)),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = paperColor,
            unfocusedContainerColor = paperColor,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = MaterialTheme.colorScheme.primary
        ),
        shape = RoundedCornerShape(12.dp),
        maxLines = Int.MAX_VALUE
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

        //mic (voice recognition)
        FloatingActionButton(
            onClick = onVoiceClick,
            shape = CircleShape,
            modifier = Modifier.size(56.dp)
        ) {
            Icon(painterResource(R.drawable.ic_microphone), contentDescription = "Voice Input")
        }

        Spacer(modifier = Modifier.weight(1f))

        FloatingActionButton(
            onClick = onSaveClick,
            shape = CircleShape,
            modifier = Modifier.size(56.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Save")
        }
    }

}


