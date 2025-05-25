package com.example.nearbynote.nearbyNoteNav

sealed class Screen(val route: String) {
    data object Main : Screen("main")
    data object WriteNoteScreen : Screen("WriteNoteScreen")

    /*    data object WorkoutGoal : Screen("workoutGoal")
        data object WorkoutEdit : Screen("workoutEdit")

        data object WorkoutGoalList : Screen("workoutGoalList/{goalType}") {
            fun createRoute(goalType: WorkoutGoalType): String = "workoutGoalList/${goalType.name}"
        }

        data object WorkoutGoalSetting : Screen("workoutGoalSetting/{workoutName}/{goalType}") {
            fun createRoute(workoutName: String, goalType: WorkoutGoalType): String =
                "workoutGoalSetting/$workoutName/${goalType.name}"
        }*/
}