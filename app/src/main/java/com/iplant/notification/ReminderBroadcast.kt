package com.iplant.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.iplant.R

class ReminderBroadcast : BroadcastReceiver() {

    /** Receive and build notification */
    override fun onReceive(context: Context, intent: Intent) {

        val plantName = intent.getStringExtra("name")
        val notificationId = intent.getIntExtra("id", 0)
        val message = intent.getStringExtra("message")
        val builder = NotificationCompat.Builder(context, "upcoming")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(plantName)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManger = NotificationManagerCompat.from(context)
        notificationManger.notify(notificationId, builder.build())
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