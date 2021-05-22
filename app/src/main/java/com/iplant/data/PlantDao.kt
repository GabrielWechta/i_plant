package com.iplant.data

import androidx.room.*
import com.iplant.data.Plant
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface PlantDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(plant: Plant)

    @Update
    suspend fun update(plant: Plant)

    @Query("SELECT * FROM plant_table WHERE NOT (:hideDead AND death_date IS NOT NULL) ORDER BY id ASC")
    fun getAllOrderedById(hideDead: Boolean): Flow<List<Plant>>

    @Query("SELECT watering_date FROM plant_table INNER JOIN watering_table ON plant_table.id = watering_table.plant_id WHERE plant_table.id = :plantId")
    fun getAllWateringDatesByPlantId(plantId: Long): List<LocalDate>

    @Query("SELECT fertilizing_date FROM plant_table INNER JOIN fertilizing_table ON plant_table.id = fertilizing_table.plant_id WHERE plant_table.id = :plantId")
    fun getAllFertilizingDatesByPlantId(plantId: Long): List<LocalDate>
}