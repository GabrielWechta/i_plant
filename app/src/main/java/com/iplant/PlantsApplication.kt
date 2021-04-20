package com.iplant

import android.app.Application
import com.iplant.data.PlantDatabase
import com.iplant.data.PlantRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class PlantsApplication : Application() {

    val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { PlantDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { PlantRepository(database.getPlantDao()) }
}