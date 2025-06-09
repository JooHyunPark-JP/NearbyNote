package com.example.nearbynote.nearbyNoteNav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.nearbynote.nearbyNoteMainFunction.geoFenceAPI.ui.GeofenceManager
import com.example.nearbynote.nearbyNoteMainFunction.geoFenceAPI.ui.GeofenceViewModel
import com.example.nearbynote.nearbyNoteMainFunction.note.ui.NoteListMain
import com.example.nearbynote.nearbyNoteMainFunction.note.ui.NoteViewModel
import com.example.nearbynote.nearbyNoteMainFunction.note.ui.WriteNoteScreen

@Composable
fun NavGraph(
    noteViewModel: NoteViewModel,
    navController: NavHostController,
    geofenceViewModel: GeofenceViewModel,
    geofenceManager: GeofenceManager
) {
    NavHost(navController = navController, startDestination = Screen.Main.route) {
        composable(Screen.Main.route) {
            NoteListMain(
                navController = navController,
                noteViewModel = noteViewModel,
                geofenceViewModel = geofenceViewModel
            )
        }

        composable(Screen.WriteNoteScreen.route) {
            WriteNoteScreen(
                navController = navController,
                noteViewModel = noteViewModel,
                geofenceViewModel = geofenceViewModel,
                geofenceManager = geofenceManager
            )
        }

    }
}
