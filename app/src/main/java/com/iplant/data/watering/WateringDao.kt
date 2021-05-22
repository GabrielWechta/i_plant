package com.iplant.data.watering

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.iplant.data.Plant

@Dao
interface WateringDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(watering: Watering)

    @Query("select * from watering_table where plant_id = :plantId order by watering_date desc limit 1")
    fun observeLastWatering(plantId: Long): LiveData<List<Watering>>

    @Query("select * from watering_table where plant_id = :plantId order by watering_date desc limit 1")
    suspend fun getLastWatering(plantId: Long): List<Watering>

    @Query("select * from watering_table where plant_id = :plantId")
    fun getAll(plantId: Long): LiveData<List<Watering>>
}