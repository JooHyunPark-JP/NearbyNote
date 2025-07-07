package com.example.nearbynote.nearbyNoteMainFunction.note.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.nearbynote.R
import com.example.nearbynote.nearbyNoteMainFunction.geoFenceAPI.data.GeofenceEntity
import com.example.nearbynote.nearbyNoteMainFunction.geoFenceAPI.ui.BasicGeofenceSetup
import com.example.nearbynote.nearbyNoteMainFunction.geoFenceAPI.ui.GeofenceManager
import com.example.nearbynote.nearbyNoteMainFunction.geoFenceAPI.ui.GeofenceViewModel
import com.example.nearbynote.nearbyNoteMainFunction.savedAddress.data.SavedAddressEntity
import com.example.nearbynote.nearbyNoteMainFunction.savedAddress.ui.SavedAddressViewModel
import com.example.nearbynote.nearbyNoteNav.Screen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
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

    val suggestions = noteViewModel.suggestions

    //val fineLocationPermission = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val googleVoicePermissionState = rememberPermissionState(Manifest.permission.RECORD_AUDIO)
    var backgroundLocationGranted by remember { mutableStateOf(false) }
    var notificationPermissionGranted by remember { mutableStateOf(false) }
    val isNotificationPermissionRequired = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    var showBackgroundDialog by remember { mutableStateOf(false) }
    var showNotificationPermissionDialog by remember { mutableStateOf(false) }

    var hasExistingGeofence by remember { mutableStateOf(false) }

    val isGeofenceImmutable by remember(noteId, hasExistingGeofence) {
        mutableStateOf(noteId != null && hasExistingGeofence)
    }

    //app knows when permission state has changed when user access to setting from an app.
    val settingsLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            backgroundLocationGranted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            notificationPermissionGranted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        }

    var isFavoriteAddress = remember { mutableStateOf(false) }
    var isFavoriteAddressDisable by remember { mutableStateOf(false) }
    val favoriteAddressName = remember { mutableStateOf("") }


    val savedAddresses by savedAddressViewModel.savedAddresses.collectAsState()
    var expanded by remember { mutableStateOf(false) }


    var selectedAddress by remember { mutableStateOf<SavedAddressEntity?>(null) }
    var isSavedAddressClicked by remember { mutableStateOf(false) }

    val shouldDisableSavedAddressRow by remember(noteId, hasExistingGeofence) {
        mutableStateOf(noteId != null && hasExistingGeofence)
    }

    // val shouldDisableSavedAddressRow = noteId != null && hasExistingGeofence

    val isAddressSearching = noteViewModel.isSearching


    val savedRowModifier = if (shouldDisableSavedAddressRow) {
        Modifier
    } else {
        Modifier.clickable { expanded = true }
    }

    fun resetNewNoteState() {
        noteText = ""

        //if user is coming from map
        if (noteViewModel.preserveMapLocation) {
            geofenceEnabled = true
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

    LaunchedEffect(Unit) {
        backgroundLocationGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (isNotificationPermissionRequired) {
            notificationPermissionGranted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
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
                .padding(top = 16.dp)
                .verticalScroll(scrollState)
        ) {

            GeofenceToggleRow(
                geofenceEnabled = geofenceEnabled,
                isGeofenceImmutable = !isGeofenceImmutable,
                onToggle = { geofenceEnabled = it }
            )

            AnimatedVisibility(visible = geofenceEnabled) {
                Column {
                    AddressSearchSection(
                        addressQuery = noteViewModel.addressQuery,
                        onQueryChange = { noteViewModel.onQueryChanged(it) },
                        suggestions = noteViewModel.suggestions,
                        onSuggestionSelected = { suggestion ->
                            geofenceViewModel.onSuggestionSelected(suggestion)
                            noteViewModel.addressQuery = suggestion.placeName
                            noteViewModel.addressLatitude = suggestion.latitude
                            geofenceViewModel.onLatitudeChanged(suggestion.latitude.toString())
                            noteViewModel.addressLongitude = suggestion.longitude
                            geofenceViewModel.onLongitudeChanged(suggestion.longitude.toString())
                            noteViewModel.suggestions = emptyList()
                        },
                        enabled = !isGeofenceImmutable && !isSavedAddressClicked,
                        isAddressSearching = isAddressSearching,
                        isSavedAddressClicked = isSavedAddressClicked,
                        noteViewModel = noteViewModel
                    )
                    if (isGeofenceImmutable) {
                        Text(
                            text = "📍 This note has a location reminder. To change location, please create a new note.",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
                        )
                    }
                }
            }

            if (geofenceEnabled) {
                //Saved address section
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = savedRowModifier.padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = if (!isSavedAddressClicked) Icons.Default.FavoriteBorder else Icons.Default.Favorite,
                        contentDescription = "Select Favorite Address",
                        tint = if (isSavedAddressClicked) Color.Red else Color.Gray
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = selectedAddress?.name ?: "Choose your favorite address",
                        color = if (!shouldDisableSavedAddressRow) Color.Black else Color.Gray
                    )

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("No favorite address") },
                            onClick = {
                                selectedAddress = null
                                expanded = false
                                noteViewModel.addressQuery = ""
                                noteViewModel.addressLatitude = 0.0
                                noteViewModel.addressLongitude = 0.0
                                geofenceViewModel.onLatitudeChanged("")
                                geofenceViewModel.onLongitudeChanged("")
                                isFavoriteAddressDisable = false
                                isSavedAddressClicked = false
                                isFavoriteAddress.value = false
                                favoriteAddressName.value = ""
                                noteViewModel.suggestions = emptyList()
                            }
                        )

                        if (savedAddresses.isNotEmpty()) {
                            DropdownMenuItem(
                                text = { HorizontalDivider() },
                                onClick = {},
                                enabled = false
                            )

                            savedAddresses.forEach { address ->
                                DropdownMenuItem(
                                    text = { Text(address.name) },
                                    onClick = {
                                        selectedAddress = address
                                        expanded = false
                                        noteViewModel.addressQuery = address.placeName
                                        noteViewModel.addressLatitude = address.latitude
                                        noteViewModel.addressLongitude = address.longitude
                                        geofenceViewModel.onLatitudeChanged(address.latitude.toString())
                                        geofenceViewModel.onLongitudeChanged(address.longitude.toString())
                                        isFavoriteAddressDisable = true
                                        isSavedAddressClicked = true
                                        isFavoriteAddress.value = false
                                        favoriteAddressName.value = ""
                                        //remove the search bar result
                                        noteViewModel.suggestions = emptyList()

                                    }
                                )
                            }
                        }
                    }
                }

                BasicGeofenceSetup(
                    geofenceViewModel = geofenceViewModel,
                    geofenceOptionsEnabled = !isGeofenceImmutable,
                    isFavoriteAddress = isFavoriteAddress,
                    favoriteAddressName = favoriteAddressName,
                    isFavoriteAddressDisable = isFavoriteAddressDisable,
                    shouldDisableSavedAddressRow = shouldDisableSavedAddressRow
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            NoteTextField(
                noteText = noteText,
                onNoteChange = { noteText = it },
                modifier = Modifier.weight(1f)
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
                        backgroundLocationGranted = backgroundLocationGranted,
                        notificationPermissionGranted = notificationPermissionGranted,
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
                        settingsLauncher.launch(intent)
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
                            appendLine("🔧 How to set it:")
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
                        settingsLauncher.launch(intent)
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
                            appendLine("🔧 How to set it:")
                            appendLine("1. Tap \"Open Settings\" below")
                            appendLine("2. Tap \"Notifications\"")
                            appendLine("4. And turn the notification on")
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

fun handleNewNoteSave(
    context: Context,
    noteText: String,
    geofenceEnabled: Boolean,
    backgroundLocationGranted: Boolean,
    notificationPermissionGranted: Boolean,
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

    if (geofenceEnabled && !backgroundLocationGranted) {
        onShowBackgroundDialog()
        return
    }


    if (geofenceEnabled && !notificationPermissionGranted && isNotificationPermissionRequired) {
        onShowNotificationDialog()
        return
    }

    if (geofenceEnabled) {
        if (lat == null || lng == null || rad == null || addressQuery.isBlank()) {
            Toast.makeText(
                context,
                "Please add address to register the location",
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
                    isVoice = false
                )

                if (isFavoriteAddress.value) {
                    savedAddressViewModel.saveAddress(
                        name = favoriteAddressName.value.ifBlank { "Unnamed" },
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
        onSuccess = {
            noteViewModel.addressQuery = ""
            navController.popBackStack()
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
fun GeofenceToggleRow(
    geofenceEnabled: Boolean,
    isGeofenceImmutable: Boolean,
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
                modifier = Modifier.fillMaxSize(),
                enabled = isGeofenceImmutable
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
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 120.dp, max = Dp.Infinity)
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
        FloatingActionButton(onClick = onVoiceClick, modifier = Modifier.size(56.dp)) {
            Icon(painterResource(R.drawable.ic_microphone), contentDescription = "Voice Input")
        }

        Spacer(modifier = Modifier.weight(1f))

        FloatingActionButton(onClick = onSaveClick, modifier = Modifier.size(56.dp)) {
            Icon(Icons.Default.Add, contentDescription = "Save")
        }
    }


}


