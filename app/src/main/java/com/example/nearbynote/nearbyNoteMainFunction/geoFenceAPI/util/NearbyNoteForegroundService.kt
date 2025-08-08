package com.example.nearbynote.nearbyNoteMainFunction.geoFenceAPI.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.nearbynote.MainActivity
import com.example.nearbynote.R
import com.example.nearbynote.nearbyNoteMainFunction.geoFenceAPI.di.GeofenceRebuildEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NearbyNoteForegroundService : Service() {

    override fun onCreate() {
        super.onCreate()
        startForeground(NOTIFICATION_ID, createNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // here, you can add more logics later such as detect location, logging etc
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotification(): Notification {
        val channelId = "foreground_service_channel"

        val channel = NotificationChannel(
            channelId,
            "NearbyNote Service",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)

        val activityIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, activityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Reminder Active")
            .setContentText("NearbyNote remind you when you're near your saved location.")
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    companion object {
        const val NOTIFICATION_ID = 1001
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        CoroutineScope(Dispatchers.IO).launch {
            val entryPoint = EntryPointAccessors.fromApplication(
                applicationContext,
                GeofenceRebuildEntryPoint::class.java
            )
            val dao = entryPoint.geofenceDao()
            val geofenceCount = dao.getAllGeofencesOnce().size

            if (geofenceCount > 0) {
                // app is being killed by swipe (or whatever reason), but if there is geofence,
                // then start the foreground system
                val serviceIntent =
                    Intent(applicationContext, NearbyNoteForegroundService::class.java)
                applicationContext.startForegroundService(serviceIntent)
            }
        }
    }
}