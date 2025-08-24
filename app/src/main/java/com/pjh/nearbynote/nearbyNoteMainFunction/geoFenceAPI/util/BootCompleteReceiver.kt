package com.pjh.nearbynote.nearbyNoteMainFunction.geoFenceAPI.util

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.pjh.nearbynote.nearbyNoteMainFunction.geoFenceAPI.di.GeofenceRebuildEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootCompleteReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val pendingResult = goAsync() // keep broadcast active while we work
        val appContext = context.applicationContext


        CoroutineScope(Dispatchers.IO).launch {
            try {
                val entryPoint = EntryPointAccessors.fromApplication(
                    appContext,
                    GeofenceRebuildEntryPoint::class.java
                )
                val geofenceDao = entryPoint.geofenceDao()
                val geofencingClient = entryPoint.geofencingClient()


                val entities = geofenceDao.getAllGeofencesOnce()
                entities.forEach { e ->
                    val geofence = Geofence.Builder()
                        .setRequestId(e.id)
                        .setCircularRegion(e.latitude, e.longitude, e.radius)
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
                        .setExpirationDuration(Geofence.NEVER_EXPIRE)
                        .setNotificationResponsiveness(15_000)
                        .build()


                    val request = GeofencingRequest.Builder()
                        .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                        .addGeofence(geofence)
                        .build()


                    if (ActivityCompat.checkSelfPermission(
                            appContext,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(
                            appContext,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        geofencingClient.addGeofences(request, getGeofencePendingIntent(appContext))
                    }
                }
            } catch (_: Exception) {
// swallow – next periodic reconcile will catch up
            } finally {
                pendingResult.finish()
            }
        }
    }


    private fun getGeofencePendingIntent(context: Context): PendingIntent =
        PendingIntent.getBroadcast(
            context,
            0,
            Intent(context, GeofenceBroadcastReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE // required for geofence extras
        )

}
