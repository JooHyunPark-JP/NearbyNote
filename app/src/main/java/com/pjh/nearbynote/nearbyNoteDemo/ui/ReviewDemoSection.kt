package com.pjh.nearbynote.nearbyNoteDemo.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pjh.nearbynote.BuildConfig

@Composable
fun ReviewDemoSection(
    onTrigger: (Long?) -> Unit,
    latestNoteId: Long?
) {
        Text("Demo section")
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedButton(
            onClick = { onTrigger(latestNoteId) },
            enabled = latestNoteId != null
        ) {
            Text(
                if (latestNoteId != null)
                    "Trigger geofence now (Review)"
                else
                    "Create a geofenced note to enable demo"
            )
        }

}