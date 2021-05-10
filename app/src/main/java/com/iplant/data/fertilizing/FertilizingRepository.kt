package com.iplant.data.fertilizing

import androidx.annotation.WorkerThread

class FertilizingRepository(private val fertilizingDao: FertilizingDao) {

    @WorkerThread
    suspend fun insert(fertilizing: Fertilizing) {
        fertilizingDao.insert(fertilizing)
    }
}