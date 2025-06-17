package com.example.nearbynote.nearbyNoteNav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.nearbynote.nearbyNoteMainFunction.geoFenceAPI.ui.GeofenceManager
import com.example.nearbynote.nearbyNoteMainFunction.geoFenceAPI.ui.GeofenceViewModel
import com.example.nearbynote.nearbyNoteMainFunction.note.ui.NoteListMain
import com.example.nearbynote.nearbyNoteMainFunction.note.ui.NoteViewModel
import com.example.nearbynote.nearbyNoteMainFunction.note.ui.WriteNoteScreen
import com.example.nearbynote.nearbyNoteMainFunction.savedAddress.ui.SavedAddressAdd
import com.example.nearbynote.nearbyNoteMainFunction.savedAddress.ui.SavedAddressMain
import com.example.nearbynote.nearbyNoteMainFunction.savedAddress.ui.SavedAddressViewModel

@Composable
fun NavGraph(
    noteViewModel: NoteViewModel,
    navController: NavHostController,
    geofenceViewModel: GeofenceViewModel,
    geofenceManager: GeofenceManager,
    savedAddressViewModel: SavedAddressViewModel
) {
    NavHost(navController = navController, startDestination = Screen.Main.route) {
        composable(Screen.Main.route) {
            NoteListMain(
                navController = navController,
                noteViewModel = noteViewModel,
                geofenceViewModel = geofenceViewModel
            )
        }

        composable(Screen.SavedAddressScreen.route) {
            SavedAddressMain(
                navController = navController,
                savedAddressViewModel = savedAddressViewModel
            )
        }

        composable(
            route = Screen.WriteNoteScreen.route, arguments = listOf(navArgument("noteId") {
                type = NavType.LongType
                defaultValue = -1L
            })
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getLong("noteId")
            WriteNoteScreen(
                navController = navController,
                noteViewModel = noteViewModel,
                geofenceViewModel = geofenceViewModel,
                geofenceManager = geofenceManager,
                savedAddressViewModel = savedAddressViewModel,
                noteId = if (noteId == -1L) null else noteId
            )
        }

        composable(Screen.SavedAddressAddScreen.route) {
            SavedAddressAdd(
                navController = navController,
                savedAddressViewModel = savedAddressViewModel,
                noteViewModel = noteViewModel
            )
        }


    }
}
