package com.pjh.nearbynote.nearbyNoteDemo.ui

import android.content.Context
import android.content.Intent
import com.pjh.nearbynote.nearbyNoteMainFunction.geoFenceAPI.util.GeofenceBroadcastReceiver

const val ACTION_DEMO_GEOFENCE_ENTER = "DEMO_GEOFENCE_ENTER"
const val EXTRA_NOTE_ID = "extra_note_id"

fun triggerGeofenceDemo(context: Context, noteId: Long?) {
    val intent = Intent(context, GeofenceBroadcastReceiver::class.java).apply {
        action = ACTION_DEMO_GEOFENCE_ENTER
        if (noteId != null) putExtra(EXTRA_NOTE_ID, noteId)
    }
    context.sendBroadcast(intent)
}