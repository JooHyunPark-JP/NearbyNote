package com.pjh.nearbynote.nearbyNoteDemo.ui

import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.pjh.nearbynote.BuildConfig

@Composable
fun ReviewDemoSection(onTrigger: (Long?) -> Unit, latestNoteId: Long?) {
    if (!BuildConfig.REVIEW_MODE) {
        OutlinedButton(onClick = { onTrigger(latestNoteId) }) {
            Text("Trigger geofence now (Review)")
        }
    }
}