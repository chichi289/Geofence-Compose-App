package com.chichi289.geofenceapp.notification

import android.content.Context
import androidx.core.app.NotificationCompat
import com.chichi289.geofenceapp.R
import com.chichi289.geofenceapp.util.getNotificationManager

class GeofenceNotification(
    private val context: Context,
    private val channel: GeofenceNotificationChannel
) {

    fun show(
        id: Int,
        title: String,
        description: String
    ) {
        val builder = buildNotification(
            title, description
        )

        context.getNotificationManager()?.notify(id, builder.build())
    }

    private fun buildNotification(
        title: String,
        description: String
    ) =
        NotificationCompat.Builder(context, channel.getChannelId()).apply {
            setSmallIcon(R.mipmap.ic_launcher)
            setContentTitle(title)
            setContentText(description)
            setAutoCancel(true)
        }

}