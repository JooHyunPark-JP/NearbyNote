package com.pjh.nearbynote.nearbyNoteMainFunction.geoFenceAPI.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

//why we need this? = Geofence might get removed when play store got updated.
//This broadcasterReceiver use geofenceRecocileworker to recover geofences after play store version got updated.
class PackageReplacedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_MY_PACKAGE_REPLACED) return
        val work = OneTimeWorkRequestBuilder<GeofenceReconcileWorker>().build()
        WorkManager.getInstance(context.applicationContext)
            .enqueueUniqueWork(
                "geofence_reconcile_now",
                ExistingWorkPolicy.REPLACE,
                work
            )
    }
}