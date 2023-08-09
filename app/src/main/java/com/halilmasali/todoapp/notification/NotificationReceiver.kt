package com.halilmasali.todoapp.notification

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.halilmasali.todoapp.R


class NotificationReceiver: BroadcastReceiver() {

    companion object {
        const val channelID = "channelID"
        const val titleExtra = "titleExtra"
        const val messageExtra = "messageExtra"
        const val notificationID = 0
    }
    override fun onReceive(context: Context, intent: Intent) {
        val notification = NotificationCompat.Builder(context, channelID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(intent.getStringExtra(titleExtra))
            .setContentText(intent.getStringExtra(messageExtra))
            .build()
        println("NotificationReceiver: $titleExtra")
        val  manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationID, notification)
    }
}