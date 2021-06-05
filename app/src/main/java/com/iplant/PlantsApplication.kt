package com.iplant

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.iplant.data.PlantDatabase
import com.iplant.data.PlantRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class PlantsApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { PlantDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { PlantRepository(database) }

    private fun createNotificationChannel() {
        val name = getString(R.string.channel_name)
        val descriptionText = getString(R.string.channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("upcoming", name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}