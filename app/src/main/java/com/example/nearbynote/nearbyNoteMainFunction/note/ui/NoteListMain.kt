package com.example.nearbynote.nearbyNoteMainFunction.note.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.nearbynote.nearbyNoteNav.Screen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NoteListMain(
    navController: NavController,
    noteViewModel: NoteViewModel,
    modifier: Modifier = Modifier
) {

    val notes by noteViewModel.notes.collectAsState()


    val locationPermissionsState = rememberMultiplePermissionsState(
        listOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )
    //   val context = LocalContext.current
    //   var showPermissionDialog by remember { mutableStateOf(false) }

    var hasLaunchedPermissionRequest by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val allGranted = locationPermissionsState.allPermissionsGranted
        val shouldShow = locationPermissionsState.permissions.any { it.status.shouldShowRationale }

        /*        val allDeniedWithoutRationale = locationPermissionsState.permissions.all {
                    it.status is PermissionStatus.Denied && !it.status.shouldShowRationale
                }*/

        when {
            allGranted -> { /* OK */
            }

            !hasLaunchedPermissionRequest -> {
                hasLaunchedPermissionRequest = true
                locationPermissionsState.launchMultiplePermissionRequest()
            }

            shouldShow -> locationPermissionsState.launchMultiplePermissionRequest()
            // allDeniedWithoutRationale -> showPermissionDialog = true
        }
    }


    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyColumn {
                items(notes) { note ->
                    Text(text = note.content, modifier = Modifier.padding(16.dp))
                }
            }
        }

        FloatingActionButton(
            onClick = {
                navController.navigate(Screen.WriteNoteScreen.route)
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Note")
        }
    }

    /*    if (showPermissionDialog) {
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
                        Text("설정 열기")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showPermissionDialog = false
                    }) {
                        Text("취소")
                    }
                },
                title = { Text("위치 권한 필요") },
                text = {
                    Text("이 앱은 위치 기반 메모 기능을 위해 위치 권한이 필요합니다. 설정에서 권한을 직접 허용해주세요.")
                }
            )
        }*/
}