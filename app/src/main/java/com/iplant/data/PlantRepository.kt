package com.iplant.data

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.iplant.data.Plant
import com.iplant.data.PlantDao
import com.iplant.data.fertilizing.Fertilizing
import com.iplant.data.watering.Watering
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class PlantRepository(private val database: PlantDatabase) {
    val plantDao = database.getPlantDao()
    val wateringDao = database.getWateringDao()
    val fertilizingDao = database.getFertilizingDao()

    val allPlants: Flow<List<Plant>> = plantDao.getAllOrderedById()

//    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(plant: Plant) {
        plantDao.insert(plant)
    }

//    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun update(plant: Plant) {
        plantDao.update(plant)
    }

    @WorkerThread
    suspend fun addWateringNote(plant: Plant, date: LocalDate) {
        val watering = Watering(plant.id, date)
        wateringDao.insert(watering)
    }

    @WorkerThread
    suspend fun addFertilizingNote(plant: Plant, date: LocalDate) {
        val fertilizing = Fertilizing(plant.id, date)
        fertilizingDao.insert(fertilizing)
    }

    fun getLastWatering(plant: Plant): LiveData<List<Watering>> {
        return wateringDao.getLastWatering(plant.id)
    }

    fun getLastFertilizing(plant: Plant): LiveData<List<Fertilizing>> {
        return fertilizingDao.getLastFertilizing(plant.id)
    }


}