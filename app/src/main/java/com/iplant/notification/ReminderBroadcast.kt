package com.iplant.notification

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.iplant.R
import com.iplant.ui.InfoActivity

class ReminderBroadcast : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val plantId = intent.getLongExtra("plant_id", -1)
        val plantName = intent.getStringExtra("name")
        val notificationId = intent.getIntExtra("id", 0)
        val notificationClickIntent = Intent(context, InfoActivity::class.java)
        notificationClickIntent.putExtra("plant_id", plantId)
        val notificationClickPendingIntent = PendingIntent.getActivity(
            context,
            2137,
            notificationClickIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        );
        val message = intent.getStringExtra("message")
        val builder = NotificationCompat.Builder(context, "upcoming")
            .setSmallIcon(R.drawable.ic_plant)
            .setContentTitle(plantName)
            .setContentText(message)
            .setContentIntent(notificationClickPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        val notificationManger = NotificationManagerCompat.from(context)
        notificationManger.notify(notificationId, builder.build())
    }
}