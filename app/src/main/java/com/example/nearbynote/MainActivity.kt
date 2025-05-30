package com.example.nearbynote

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
import androidx.navigation.compose.rememberNavController
import com.example.nearbynote.nearbyNoteMainFunction.geoFenceAPI.ui.GeofenceViewModel
import com.example.nearbynote.nearbyNoteMainFunction.note.ui.NoteViewModel
import com.example.nearbynote.nearbyNoteNav.BottomNavBar
import com.example.nearbynote.nearbyNoteNav.NavGraph
import com.example.nearbynote.nearbyNoteNav.TopBar
import com.example.nearbynote.ui.theme.NearbyNoteTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val noteViewModel: NoteViewModel by viewModels()
        val geofenceViewModel: GeofenceViewModel by viewModels()

        setContent {
            NearbyNoteTheme {
                val navController = rememberNavController()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopBar(navController)
                    },
                    bottomBar = {
                        BottomNavBar(navController = navController)

                    }) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding)) {
                        NavGraph(
                            navController = navController,
                            noteViewModel = noteViewModel,
                            geofenceViewModel = geofenceViewModel
                        )
                    }
                }
            }
        }
    }
}