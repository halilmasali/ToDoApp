package com.halilmasali.todoapp.notification


import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import android.Manifest
import android.os.Build
import androidx.core.app.ActivityCompat
import com.halilmasali.todoapp.ui.MainActivity

class Notification(private val context: Context) {

    init {
        getNotificationPermission()
        val name = "Notification Channel"
        val desc = "A Description of the Channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(NotificationReceiver.channelID, name, importance)
        channel.description = desc
        val notificationManager =
            context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }


    fun scheduleNotification(title: String, message: String, time: Long) {
        val intent = Intent(context, NotificationReceiver::class.java)
        intent.putExtra(NotificationReceiver.titleExtra, title)
        intent.putExtra(NotificationReceiver.messageExtra, message)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            NotificationReceiver.notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
        //alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent)
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            println(alarmManager.canScheduleExactAlarms())
        }
    }


    private fun getNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.SCHEDULE_EXACT_ALARM)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    context as MainActivity,
                    arrayOf(Manifest.permission.SCHEDULE_EXACT_ALARM),
                    1
                )
            }
        }
    }
}