package com.iplant.data

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
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

    fun getAllPlants(hideDead: Boolean): Flow<List<Plant>> = plantDao.getAllOrderedById(hideDead)

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

    fun observeLastWatering(plant: Plant): LiveData<List<Watering>> {
        return wateringDao.observeLastWatering(plant.id)
    }

    suspend fun getLastWatering(plant: Plant): Watering? {
        val watering = wateringDao.getLastWatering(plant.id)
        return if (watering.isNotEmpty()) watering[0] else null
    }

    private fun getAllWatering(plant: Plant): LiveData<List<Watering>> {
        return wateringDao.getAll(plant.id)
    }

    fun observeLastFertilizing(plant: Plant): LiveData<List<Fertilizing>> {
        return fertilizingDao.observeLastFertilizing(plant.id)
    }

    suspend fun getLastFertilizing(plant: Plant): Fertilizing? {
        val fertilizing = fertilizingDao.getLastFertilizing(plant.id)
        return if (fertilizing.isNotEmpty()) fertilizing[0] else null
    }

    private fun getAllFertilizing(plant: Plant): LiveData<List<Fertilizing>> {
        return fertilizingDao.getAll(plant.id)
    }

    fun getAllEvents(plant: Plant): LiveData<List<PlantEvent>> {
        val combined = object: MediatorLiveData<List<PlantEvent>>() {
            var wateringList: List<Watering> = listOf()
            var fertilizingList: List<Fertilizing> = listOf()
        }
        val watering = getAllWatering(plant)
        val fertilizing = getAllFertilizing(plant)
        combined.addSource(watering) {
            combined.wateringList = it
            combined.value = it + combined.fertilizingList
        }
        combined.addSource(fertilizing) {
            combined.fertilizingList = it
            combined.value = combined.wateringList + it
        }
        return combined
    }


}