package com.pjh.nearbynote.nearbyNoteMainFunction.geoFenceAPI.util

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.pjh.nearbynote.nearbyNoteMainFunction.geoFenceAPI.di.GeofenceRebuildEntryPoint
import dagger.hilt.android.EntryPointAccessors
import java.util.concurrent.TimeUnit

//Why we need this?: Geofences can be dropped across reboots/updates. A lightweight daily reconcile keeps them consistent.
class GeofenceReconcileWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val app = applicationContext
        val entryPoint =
            EntryPointAccessors.fromApplication(app, GeofenceRebuildEntryPoint::class.java)
        val geofenceDao = entryPoint.geofenceDao()
        val geofencingClient = entryPoint.geofencingClient()


        val entities = geofenceDao.getAllGeofencesOnce()
        if (entities.isEmpty()) return Result.success()


        if (ActivityCompat.checkSelfPermission(
                app,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                app,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return Result.success() // nothing we can do without permission
        }

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


            geofencingClient.addGeofences(request, getGeofencePendingIntent(app))
        }
        return Result.success()
    }

    companion object {
        private const val UNIQUE_NAME = "geofence_reconcile_daily"
        fun schedule(appContext: Context) {
            val work = PeriodicWorkRequestBuilder<GeofenceReconcileWorker>(1, TimeUnit.DAYS)
                .build()
            WorkManager.getInstance(appContext)
                .enqueueUniquePeriodicWork(UNIQUE_NAME, ExistingPeriodicWorkPolicy.UPDATE, work)
        }
    }
}


private fun getGeofencePendingIntent(context: Context): PendingIntent =
    PendingIntent.getBroadcast(
        context,
        0,
        Intent(context, GeofenceBroadcastReceiver::class.java),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
    )