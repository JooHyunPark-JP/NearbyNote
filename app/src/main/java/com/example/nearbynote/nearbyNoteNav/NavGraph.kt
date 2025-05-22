package com.example.nearbynote.nearbyNoteNav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.nearbynote.nearbyNoteMainFunction.note.NoteListMain
import com.example.nearbynote.nearbyNoteMainFunction.note.WriteNoteScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    ) {
    NavHost(navController = navController, startDestination = Screen.Main.route) {
        composable(Screen.Main.route) {
            NoteListMain(
                navController,
            )
        }

        composable(Screen.WriteNoteScreen.route) {
            WriteNoteScreen(navController)
        }

        /*        composable(Screen.Diet.route) {
                    DietScreen(viewModel = dietViewModel)
                }

                composable(Screen.Sleep.route) {
                    SleepScreen(viewModel = sleepViewModel)
                }

                composable(Screen.WorkoutGoal.route) {
                    WorkoutGoalScreen(viewModel = workoutViewModel, navController = navController)
                }

                composable(Screen.Profile.route) {
                    ProfileScreen(loginViewModel = loginViewModel)
                }*/


    }
}
