package com.pjh.nearbynote.nearbyNoteMainFunction.geoFenceAPI.util

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.pjh.nearbynote.nearbyNoteMainFunction.geoFenceAPI.di.GeofenceRebuildEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class BootCompleteReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val appContext = context.applicationContext


            val entryPoint = EntryPointAccessors.fromApplication(
                appContext,
                GeofenceRebuildEntryPoint::class.java
            )

            val geofenceDao = entryPoint.geofenceDao()
            val geofencingClient = entryPoint.geofencingClient()
            val scope = CoroutineScope(Dispatchers.IO)

            scope.launch {
                val geofences = geofenceDao.getAllGeofencesOnce()

                geofences.forEach { entity ->
                    val geofence = Geofence.Builder()
                        .setRequestId(entity.id)
                        .setCircularRegion(entity.latitude, entity.longitude, entity.radius)
                        .setExpirationDuration(Geofence.NEVER_EXPIRE)
                        .setTransitionTypes(
                            Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT
                        )
                        .setNotificationResponsiveness(15_000)
                        .build()

                    val request = GeofencingRequest.Builder()
                        .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                        .addGeofence(geofence)
                        .build()

                    //Todo: don't we need background permission check here?
                    if (ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        try {
                            geofencingClient.addGeofences(
                                request,
                                getGeofencePendingIntent(appContext)
                            ).await()
                        } catch (e: Exception) {
                            /*                            Log.e(
                                                            "BootReceiver",
                                                            "Failed to re-add geofence ${entity.id}: ${e.message}"
                                                        )*/
                        }
                    } else {
                        Log.w(
                            "BootReceiver",
                            "Location permission not granted, skipping geofence ${entity.id}"
                        )
                    }
                }
            }
        }
    }

    private fun getGeofencePendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
        return PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }
}
