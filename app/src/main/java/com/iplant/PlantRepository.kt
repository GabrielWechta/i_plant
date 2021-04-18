package com.iplant

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class PlantRepository(private val plantDao: PlantDao) {
    val allPlants: Flow<List<Plant>> = plantDao.getAllById()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(plant: Plant) {
        plantDao.insert(plant)
    }
}