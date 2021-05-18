package com.iplant.ui

import androidx.lifecycle.*
import com.iplant.data.Plant
import com.iplant.data.PlantRepository
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class PlantViewModel(private val repository: PlantRepository) : ViewModel() {
    val allPlants: LiveData<List<Plant>> = repository.allPlants.asLiveData()

    fun insert(plant: Plant) = viewModelScope.launch {
        repository.insert(plant)
    }

    fun update(plant: Plant) = viewModelScope.launch {
        repository.update(plant)
    }

    fun getLastWatering(plant: Plant) = repository.getLastWatering(plant)

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