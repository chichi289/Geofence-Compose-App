package com.chichi289.geofenceapp.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.chichi289.geofenceapp.util.CHANNEL_DESCRIPTION
import com.chichi289.geofenceapp.util.CHANNEL_NAME
import com.chichi289.geofenceapp.util.getNotificationManager

class GeofenceNotificationChannel(context: Context) {

    companion object {
        const val CHANNEL_ID = "geofence_notification_channel"
    }

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = CHANNEL_NAME
            val description = CHANNEL_DESCRIPTION
            val importance = NotificationManager.IMPORTANCE_HIGH

            NotificationChannel(CHANNEL_ID, name, importance).apply {
                this.description = description
                context.getNotificationManager()?.createNotificationChannel(this)
            }
        }
    }

    fun getChannelId() = CHANNEL_ID

}