package com.example.nearbynote.nearbyNoteMainFunction.geoFenceAPI.ui

import android.content.Context
import android.location.Geocoder
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.resume

class GeofenceManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
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
}