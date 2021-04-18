package com.iplant

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(plant: Plant)

    @Update
    suspend fun update(plant: Plant)

    @Query("SELECT * FROM plant_table ORDER BY id ASC")
    fun getAllById() : Flow<List<Plant>>
}