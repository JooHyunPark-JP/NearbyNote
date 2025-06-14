package com.example.nearbynote.nearbyNoteMainFunction.geoFenceAPI.ui

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.os.Build
import android.util.Log
import com.example.nearbynote.nearbyNoteMainFunction.geoFenceAPI.util.GeofenceBroadcastReceiver
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.resume

class GeofenceManager @Inject constructor(
    @ApplicationContext val context: Context,
    private val geofencingClient: GeofencingClient
) {

    private val activeGeofences = mutableListOf<String>()

    suspend fun getAddressFromLatLng(lat: Double, lng: Double): String {
        return withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    suspendCancellableCoroutine { cont ->
                        geocoder.getFromLocation(lat, lng, 1) { addresses ->
                            val address = addresses?.firstOrNull()?.getAddressLine(0)
                            cont.resume(address ?: "No address found")
                        }
                    }
                } else {
                    val addresses = geocoder.getFromLocation(lat, lng, 1)
                    addresses?.firstOrNull()?.getAddressLine(0) ?: "No address found"
                }
            } catch (e: Exception) {
                "Error: ${e.message}"
            }
        }
    }


    //suppress missing permission here because it is checking already in UI level.
    //Check BasicGeofenceSetup.kt
    @SuppressLint("MissingPermission")
    fun addGeofence(
        geofenceId: String,
        latitude: Double,
        longitude: Double,
        radius: Float = 100f,
        onSuccess: () -> Unit = {},
        onFailure: (Throwable) -> Unit = {}
    ) {
        val geofence = Geofence.Builder()
            .setRequestId(geofenceId)
            .setCircularRegion(latitude, longitude, radius)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
            .build()

        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        geofencingClient.addGeofences(geofencingRequest, getGeofencePendingIntent(context))
            .addOnSuccessListener {
                Log.d("GeofenceManager", "✅ Geofence added: $geofenceId")
                activeGeofences.add(geofenceId)
                Log.d("GeofenceManager", "📋 All registered geofences: $activeGeofences")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("GeofenceManager", "❌ Failed to add geofence: ${e.message}")
                onFailure(e)
            }
    }

    fun removeAllGeofences(
        onSuccess: () -> Unit = {},
        onFailure: (Throwable) -> Unit = {},
        context: Context
    ) {
        geofencingClient.removeGeofences(getGeofencePendingIntent(context))
            .addOnSuccessListener {
                Log.d("GeofenceManager", "All geofences removed.")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("GeofenceManager", "Failed to remove geofences: ${e.message}")
                onFailure(e)
            }
    }

    fun removeGeofenceById(
        geofenceId: String,
        onSuccess: () -> Unit = {},
        onFailure: (Throwable) -> Unit = {},
        context: Context
    ) {
        geofencingClient.removeGeofences(listOf(geofenceId))
            .addOnSuccessListener {
                Log.d("GeofenceManager", "✅ Geofence removed: $geofenceId")
                activeGeofences.remove(geofenceId)
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("GeofenceManager", "❌ Failed to remove geofence $geofenceId: ${e.message}")
                onFailure(e)
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
