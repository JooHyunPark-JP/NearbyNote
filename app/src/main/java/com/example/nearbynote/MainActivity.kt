package com.example.nearbynote

import android.content.Intent
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
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.nearbynote.nearbyNoteMainFunction.geoFenceAPI.ui.GeofenceManager
import com.example.nearbynote.nearbyNoteMainFunction.geoFenceAPI.ui.GeofenceViewModel
import com.example.nearbynote.nearbyNoteMainFunction.geoFenceAPI.util.NearbyNoteForegroundService
import com.example.nearbynote.nearbyNoteMainFunction.mapBoxAPI.ui.MapboxViewModel
import com.example.nearbynote.nearbyNoteMainFunction.note.ui.NoteViewModel
import com.example.nearbynote.nearbyNoteMainFunction.savedAddress.ui.SavedAddressViewModel
import com.example.nearbynote.nearbyNoteNav.BottomNavBar
import com.example.nearbynote.nearbyNoteNav.NavGraph
import com.example.nearbynote.nearbyNoteNav.Screen
import com.example.nearbynote.nearbyNoteNav.TopBar
import com.example.nearbynote.ui.theme.NearbyNoteTheme
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
            val hasGeofence = geofenceViewModel.hasAnyGeofenceRegistered()
            if (hasGeofence) {
                val serviceIntent =
                    Intent(applicationContext, NearbyNoteForegroundService::class.java)
                ContextCompat.startForegroundService(applicationContext, serviceIntent)
            }


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