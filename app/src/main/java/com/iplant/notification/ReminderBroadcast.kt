package com.iplant.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.iplant.R
import com.iplant.data.Plant

class ReminderBroadcast : BroadcastReceiver() {

    /** Receive and build notification */
    override fun onReceive(context: Context, intent: Intent ) {

        var plant = intent.getParcelableExtra<Plant>("plant")
        var builder = NotificationCompat.Builder(context, R.string.reminder_channel.toString())
            .setSmallIcon(R.drawable.ic_notyfication)
            .setContentTitle(plant?.caressing_name)
            .setContentText("Description")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        var notificationManger =NotificationManagerCompat.from(context)
        notificationManger.notify(200,builder.build())
    }

//    USE IN FUTURE TO SET NOTIFICATION

//    var intent = Intent(this, ReminderBroadcast::class.java)
//    var pendingIntent = PendingIntent.getBroadcast(this,0,intent,0)
//
//
//    var alarmManager : AlarmManager = getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager
//    alarmManager.set(AlarmManager.RTC_WAKEUP, ZonedDateTime.of(item.date, ZoneId.systemDefault()).toInstant().toEpochMilli()-3600000,pendingIntent)
//    finish()
}