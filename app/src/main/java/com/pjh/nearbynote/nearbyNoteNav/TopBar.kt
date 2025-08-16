package com.pjh.nearbynote.nearbyNoteNav

import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.pjh.nearbynote.nearbyNoteMainFunction.geoFenceAPI.ui.GeofenceViewModel
import com.pjh.nearbynote.nearbyNoteMainFunction.note.ui.NoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    navController: NavHostController,
    noteViewModel: NoteViewModel,
    geofenceViewModel: GeofenceViewModel
) {

    val context = LocalContext.current
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination?.route

    val noteIdArg = currentBackStackEntry?.arguments?.getLong("noteId")?.takeIf { it != -1L }

    val topBarColors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.inversePrimary
    )


    var showDeleteDialog by remember { mutableStateOf(false) }

    when (currentDestination) {
        Screen.Main.route -> {
            CenterAlignedTopAppBar(
                title = { Text("Your Note List") },
                colors = topBarColors
            )
        }

        Screen.WriteNoteScreen.route -> {
            CenterAlignedTopAppBar(
                title = { Text("Write Note") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (noteIdArg != null) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Note")
                        }
                    }
                },
                colors = topBarColors
            )
        }

        Screen.SavedAddressScreen.route -> {
            CenterAlignedTopAppBar(
                title = { Text("Favorite Address") },
                colors = topBarColors
            )
        }

        Screen.SavedAddressAddScreen.route -> {
            CenterAlignedTopAppBar(
                title = { Text("Add favorite address") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = topBarColors
            )
        }

        Screen.PermissionStatusScreen.route -> {
            CenterAlignedTopAppBar(
                title = { Text("Permission Status") },
                colors = topBarColors
            )
        }

        Screen.ReadNoteScreen.route -> {
            CenterAlignedTopAppBar(
                title = { Text("Your Saved Note") },
                colors = topBarColors,
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }

        else -> {
            CenterAlignedTopAppBar(
                title = { Text("NearbyNote Map") },
                colors = topBarColors
            )
        }


    }
    if (showDeleteDialog && noteIdArg != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    noteViewModel.deleteNoteAndGeofence(noteIdArg, geofenceViewModel)
                    Toast.makeText(context, "Note deleted!", Toast.LENGTH_SHORT).show()
                    showDeleteDialog = false
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Main.route) { inclusive = true }
                    }
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            },
            title = { Text("Delete note?") },
            text = { Text("Are you sure you want to delete this note?") }
        )
    }
}