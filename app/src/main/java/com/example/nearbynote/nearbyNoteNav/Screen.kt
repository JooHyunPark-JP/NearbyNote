package com.example.nearbynote.nearbyNoteNav

sealed class Screen(val route: String) {
    data object Main : Screen("main")
    data object WriteNoteScreen : Screen("write_note_screen/{noteId}") {
        fun routeWithNoteId(noteId: Long?) = "write_note_screen/${noteId ?: -1L}"
    }
}