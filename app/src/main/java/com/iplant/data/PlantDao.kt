package com.iplant.data

import androidx.room.*
import com.iplant.data.Plant
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(plant: Plant)

    @Update
    suspend fun update(plant: Plant)

    @Query("SELECT * FROM plant_table ORDER BY id ASC")
    fun getAllOrderedById() : Flow<List<Plant>>
}