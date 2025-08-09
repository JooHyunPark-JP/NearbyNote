package com.pjh.nearbynote.nearbyNoteNav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.pjh.nearbynote.nearbyNoteMainFunction.geoFenceAPI.ui.GeofenceManager
import com.pjh.nearbynote.nearbyNoteMainFunction.geoFenceAPI.ui.GeofenceViewModel
import com.pjh.nearbynote.nearbyNoteMainFunction.mapBoxAPI.ui.MapboxScreen
import com.pjh.nearbynote.nearbyNoteMainFunction.mapBoxAPI.ui.MapboxViewModel
import com.pjh.nearbynote.nearbyNoteMainFunction.note.ui.NoteListMain
import com.pjh.nearbynote.nearbyNoteMainFunction.note.ui.NoteViewModel
import com.pjh.nearbynote.nearbyNoteMainFunction.note.ui.ReadNoteScreen
import com.pjh.nearbynote.nearbyNoteMainFunction.note.ui.WriteNoteScreen
import com.pjh.nearbynote.nearbyNoteMainFunction.permissionStatus.ui.PermissionStatusScreen
import com.pjh.nearbynote.nearbyNoteMainFunction.savedAddress.ui.SavedAddressAdd
import com.pjh.nearbynote.nearbyNoteMainFunction.savedAddress.ui.SavedAddressMain
import com.pjh.nearbynote.nearbyNoteMainFunction.savedAddress.ui.SavedAddressViewModel

@Composable
fun NavGraph(
    startDestination: String,
    noteViewModel: NoteViewModel,
    navController: NavHostController,
    geofenceViewModel: GeofenceViewModel,
    geofenceManager: GeofenceManager,
    savedAddressViewModel: SavedAddressViewModel,
    mapboxViewModel: MapboxViewModel,
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Main.route) {
            NoteListMain(
                navController = navController,
                noteViewModel = noteViewModel,
                geofenceViewModel = geofenceViewModel,
                savedAddressViewModel = savedAddressViewModel
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
                noteViewModel = noteViewModel,
                geofenceViewModel = geofenceViewModel
            )
        }

        composable(Screen.MapboxScreen.route) {
            MapboxScreen(
                navController = navController,
                noteViewModel = noteViewModel,
                geofenceViewModel = geofenceViewModel,
                mapboxViewModel = mapboxViewModel,
                geofenceManager = geofenceManager
            )
        }


        composable(Screen.PermissionStatusScreen.route) {
            PermissionStatusScreen(navController = navController)
        }

        composable(
            route = Screen.ReadNoteScreen.route, arguments = listOf(navArgument("noteId") {
                type = NavType.LongType
                defaultValue = -1L
            })
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getLong("noteId")
            ReadNoteScreen(
                navController = navController,
                noteViewModel = noteViewModel,
                noteId = if (noteId == -1L) null else noteId,
                savedAddressViewModel = savedAddressViewModel
            )
        }
    }
}
