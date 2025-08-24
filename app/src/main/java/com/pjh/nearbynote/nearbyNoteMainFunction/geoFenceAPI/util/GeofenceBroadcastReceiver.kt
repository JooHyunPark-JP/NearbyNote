package com.pjh.nearbynote.nearbyNoteMainFunction.geoFenceAPI.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.pjh.nearbynote.MainActivity
import com.pjh.nearbynote.R
import com.pjh.nearbynote.nearbyNoteDemo.ui.ACTION_DEMO_GEOFENCE_ENTER
import com.pjh.nearbynote.nearbyNoteDemo.ui.EXTRA_NOTE_ID
import com.pjh.nearbynote.nearbyNoteMainFunction.geoFenceAPI.di.NoteDaoEntryPoint
import com.pjh.nearbynote.nearbyNoteMainFunction.note.data.NoteEntity
import com.pjh.nearbynote.nearbyNoteMainFunction.note.data.NoteRepository
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        //First take care of demo version id first (for testers)
        if (intent.action == ACTION_DEMO_GEOFENCE_ENTER) {
            val noteId = intent.getLongExtra(
                EXTRA_NOTE_ID, -1L
            ).takeIf { it > 0L }

            if (noteId != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    val appContext = context.applicationContext
                    val hiltEntryPoint = EntryPointAccessors.fromApplication(
                        appContext,
                        NoteDaoEntryPoint::class.java
                    )
                    val noteDao = hiltEntryPoint.noteDao()
                    val noteRepository = NoteRepository(noteDao)

                    val note = noteRepository.getNoteById(noteId)

                    withContext(Dispatchers.Main) {
                        note?.let {
                            sendNotification(appContext, /* isInside = */ true, it)
                        }
                    }
                }
            } else {
                // If no note ID, send this message to testers.
                Toast.makeText(
                    context,
                    "Please select a saved geofence note first.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            // Handling demo ends. Code doesn't go below from here
            return
        }


        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent?.hasError() == true) return

        val triggeringGeofenceId =
            geofencingEvent?.triggeringGeofences?.firstOrNull()?.requestId ?: return

        val geofenceTransition = geofencingEvent.geofenceTransition
        if (geofenceTransition != Geofence.GEOFENCE_TRANSITION_ENTER) return

        val isInside = when (geofenceTransition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> true
            else -> false
        }

        CoroutineScope(Dispatchers.IO).launch {
            val appContext = context.applicationContext
            val hiltEntryPoint = EntryPointAccessors.fromApplication(
                appContext,
                NoteDaoEntryPoint::class.java
            )
            val noteDao = hiltEntryPoint.noteDao()
            val noteRepository = NoteRepository(noteDao)
            val note = noteRepository.getNoteByGeofenceId(triggeringGeofenceId)

            withContext(Dispatchers.Main) {
                note?.let {
                    sendNotification(appContext, isInside, it)
                }
            }
        }
    }
}


private fun sendNotification(context: Context, isInside: Boolean, note: NoteEntity) {
    val channelId = "geofence_channel"
    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        putExtra("noteId", note.id)
    }

    val pendingIntent = PendingIntent.getActivity(
        context, note.id.toInt(), intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )


    /*        val title = if (isInside)
                "📍 Your Saved Note!"
            else
                "📍 Left: ${note.locationName.orEmpty()}"*/

    val title = if (isInside)
        "📍 Your Saved Note!"
    else
        "📍 Left: ${note.locationName.orEmpty()}"

    val formattedDate = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
        .format(Date(note.createdAt))

    val cancelIntent = Intent(context, CancelNotificationReceiver::class.java).apply {
        putExtra("notificationId", note.id.toInt())
    }
    val cancelPendingIntent = PendingIntent.getBroadcast(
        context, note.id.toInt(), cancelIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.ic_notification)
        .setContentTitle(title)
        .setContentText(note.content)
        .setSubText("Created: $formattedDate")
        .setStyle(NotificationCompat.BigTextStyle().bigText(note.content))
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
        .setContentIntent(pendingIntent)
        .addAction(0, "Read Note", pendingIntent)
        .addAction(0, "Cancel", cancelPendingIntent)
        .build()

    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val channel = NotificationChannel(
        channelId,
        "Geofence Notifications",
        NotificationManager.IMPORTANCE_HIGH
    )
    manager.createNotificationChannel(channel)
    manager.notify(note.id.toInt(), notification)
}
