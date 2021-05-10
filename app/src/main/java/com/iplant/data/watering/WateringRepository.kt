package com.iplant.data.watering

import androidx.annotation.WorkerThread

class WateringRepository(private val wateringDao: WateringDao) {

    @WorkerThread
    suspend fun insert(watering: Watering) {
        wateringDao.insert(watering)
    }
}