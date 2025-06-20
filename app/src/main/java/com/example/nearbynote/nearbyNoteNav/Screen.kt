package com.example.nearbynote.nearbyNoteNav

sealed class Screen(val route: String) {
    data object Main : Screen("main")
    data object WriteNoteScreen : Screen("write_note_screen/{noteId}") {
        fun routeWithNoteId(noteId: Long?) = "write_note_screen/${noteId ?: -1L}"
    }
    data object SavedAddressScreen : Screen("saved_address_screen")
    data object SavedAddressAddScreen : Screen("saved_address_add_screen")
}