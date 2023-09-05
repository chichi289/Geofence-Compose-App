package com.chichi289.geofenceapp.geofence

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun GeofenceBroadcastReceiver(
    systemAction: String,
    onEvent: (
        triggeringGeofences: List<Geofence>?,
        geofenceTransition: Int,
        timestamp: String
    ) -> Unit
) {
    val context = LocalContext.current
    val currentSystemOnEvent by rememberUpdatedState(onEvent)

    DisposableEffect(context, systemAction) {
        val intentFilter = IntentFilter(systemAction)
        val broadcast = object : BroadcastReceiver() {

            fun formatMilliseconds(milliseconds: Long): String {
                val totalSeconds = milliseconds / 1000
                val minutes = totalSeconds / 60
                val seconds = totalSeconds % 60
                return "$minutes:$seconds"
            }

            private fun getDateTime(timestamp: Long): String {
                return try {
                    val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.ENGLISH)
                    val netDate = Date(timestamp)
                    sdf.format(netDate)
                } catch (e: Exception) {
                    e.toString()
                }
            }

            override fun onReceive(context: Context?, intent: Intent?) {
                val geofencingEvent = intent?.let { GeofencingEvent.fromIntent(it) } ?: return

                if (geofencingEvent.hasError()) {
                    val errorMessage =
                        GeofenceStatusCodes.getStatusCodeString(geofencingEvent.errorCode)
                    Log.e("GeofenceReceiver", "onReceive: $errorMessage")
                    return
                }

                currentSystemOnEvent(
                    geofencingEvent.triggeringGeofences,
                    geofencingEvent.geofenceTransition,
                    getDateTime(System.currentTimeMillis())
                )
            }
        }
        context.registerReceiver(broadcast, intentFilter)
        onDispose {
            context.unregisterReceiver(broadcast)
        }
    }

}