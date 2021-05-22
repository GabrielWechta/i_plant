package com.iplant.ui

import androidx.lifecycle.*
import com.iplant.data.Plant
import com.iplant.data.PlantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import java.time.LocalDate

class PlantViewModel(private val repository: PlantRepository) : ViewModel() {
    val hideDead = MutableStateFlow(false)
    val allPlants: LiveData<List<Plant>> = hideDead.flatMapLatest {
        repository.getAllPlants(hideDead = it)
    }.asLiveData()


    fun insert(plant: Plant) = viewModelScope.launch {
        repository.insert(plant)
    }

    fun update(plant: Plant) = viewModelScope.launch {
        repository.update(plant)
    }

    fun addWateringNote(plant: Plant, date: LocalDate) = viewModelScope.launch {
        repository.addWateringNote(plant, date)
    }

    fun addFertilizingNote(plant: Plant, date: LocalDate) = viewModelScope.launch {
        repository.addFertilizingNote(plant, date)
    }

    fun observeLastWatering(plant: Plant) = repository.observeLastWatering(plant)
    suspend fun getLastWatering(plant: Plant) = repository.getLastWatering(plant)

    fun observeLastFertilizing(plant: Plant) = repository.observeLastFertilizing(plant)
    suspend fun getLastFertilizing(plant: Plant) = repository.getLastFertilizing(plant)

    fun getAllEvents(plant: Plant) = repository.getAllEvents(plant)


}

class PlantViewModelFactory(private val repository: PlantRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlantViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlantViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}