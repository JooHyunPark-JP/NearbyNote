package com.pjh.nearbynote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.pjh.nearbynote.nearbyNoteMainFunction.geoFenceAPI.ui.GeofenceManager
import com.pjh.nearbynote.nearbyNoteMainFunction.geoFenceAPI.ui.GeofenceViewModel
import com.pjh.nearbynote.nearbyNoteMainFunction.geoFenceAPI.util.GeofenceReconcileWorker
import com.pjh.nearbynote.nearbyNoteMainFunction.mapBoxAPI.ui.MapboxViewModel
import com.pjh.nearbynote.nearbyNoteMainFunction.note.ui.NoteViewModel
import com.pjh.nearbynote.nearbyNoteMainFunction.savedAddress.ui.SavedAddressViewModel
import com.pjh.nearbynote.nearbyNoteNav.BottomNavBar
import com.pjh.nearbynote.nearbyNoteNav.NavGraph
import com.pjh.nearbynote.nearbyNoteNav.Screen
import com.pjh.nearbynote.nearbyNoteNav.TopBar
import com.pjh.nearbynote.ui.theme.NearbyNoteTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    @Inject
    lateinit var geofenceManager: GeofenceManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        GeofenceReconcileWorker.schedule(this)

        val noteViewModel: NoteViewModel by viewModels()
        val geofenceViewModel: GeofenceViewModel by viewModels()
        val savedAddressViewModel: SavedAddressViewModel by viewModels()
        val mapboxViewModel: MapboxViewModel by viewModels()

        val noteIdFromIntent = intent?.getLongExtra("noteId", -1L)
        val startDestination = if (noteIdFromIntent != null && noteIdFromIntent != -1L) {
            noteViewModel.isAddressSelected = true
            // Screen.WriteNoteScreen.routeWithNoteId(noteIdFromIntent)
            Screen.ReadNoteScreen.routeWithNoteId(noteIdFromIntent)
        } else {
            Screen.Main.route
        }

        lifecycleScope.launch {
            setContent {
                NearbyNoteTheme {
                    val navController = rememberNavController()

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            TopBar(
                                navController = navController,
                                noteViewModel = noteViewModel,
                                geofenceViewModel = geofenceViewModel
                            )

                        },
                        bottomBar = {
                            BottomNavBar(navController = navController)

                        }) { innerPadding ->
                        Column(modifier = Modifier.padding(innerPadding)) {
                            NavGraph(
                                startDestination = startDestination,
                                navController = navController,
                                noteViewModel = noteViewModel,
                                geofenceViewModel = geofenceViewModel,
                                geofenceManager = geofenceManager,
                                savedAddressViewModel = savedAddressViewModel,
                                mapboxViewModel = mapboxViewModel,

                                )
                        }
                    }
                }
            }
        }
    }
}