package com.example.nearbynote.nearbyNoteNav

sealed class Screen(val route: String, val label: String) {
    data object Main : Screen("main", "Note Lists")
    data object WriteNoteScreen : Screen("write_note_screen/{noteId}", "Write") {
        fun routeWithNoteId(noteId: Long?) = "write_note_screen/${noteId ?: -1L}"
    }

    data object SavedAddressScreen : Screen("saved_address_screen", "Favorite")
    data object SavedAddressAddScreen : Screen("saved_address_add_screen", "Add Favorite")
    data object MapboxScreen : Screen("mapbox_screen", "Map")
    data object PermissionStatusScreen : Screen("permission_status_screen", "Permission")

    data object ReadNoteScreen : Screen("read_note_screen/{noteId}", "Read Note") {
        fun routeWithNoteId(noteId: Long?) = "read_note_screen/${noteId ?: -1L}"
    }
}