package com.iplant.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.iplant.data.Plant
import java.time.LocalDate
import java.time.ZoneOffset

object NotificationsMaker {
    fun makeWateringNotification(
        context: Context,
        plant: Plant,
        alarmManager: AlarmManager
    ) {
        val intentWatering = Intent(context, ReminderBroadcast::class.java)
        intentWatering.putExtra("message", "Your plant needs watering")
        intentWatering.putExtra("id", plant.id.toInt() * 2)
        intentWatering.putExtra("name", plant.caressing_name)
        intentWatering.putExtra("plant_id", plant.id)

        val pendingIntentWatering =
            PendingIntent.getBroadcast(
                context,
                0,
                intentWatering,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            LocalDate.now().plusDays(plant.watering_period.toLong()).atTime(10, 0).toEpochSecond(
                ZoneOffset.UTC
            ) * 1000,
            pendingIntentWatering
        )
    }

    fun makeFertilizingNotification(context: Context, plant: Plant, alarmManager: AlarmManager) {
        val intentFertilizing = Intent(context, ReminderBroadcast::class.java)
        intentFertilizing.putExtra("message", "Your plant needs fertilizing")
        intentFertilizing.putExtra("id", plant.id.toInt() * 2 + 1)
        intentFertilizing.putExtra("name", plant.caressing_name)
        intentFertilizing.putExtra("plant_id", plant.id)

        val pendingIntentFertilizing =
            PendingIntent.getBroadcast(
                context,
                1,
                intentFertilizing,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            LocalDate.now().plusDays(plant.fertilizing_period.toLong()).atTime(10, 0)
                .toEpochSecond(
                    ZoneOffset.UTC
                ) * 1000,
            pendingIntentFertilizing
        )
    }

}