package com.iplant.data.fertilizing

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.iplant.data.watering.Watering

@Dao
interface FertilizingDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(fertilizing: Fertilizing)

    @Query("select * from fertilizing_table where plant_id = :plantId order by fertilizing_date desc limit 1")
    fun getLastFertilizing(plantId: Long): LiveData<List<Fertilizing>>

    @Query("select * from fertilizing_table where plant_id = :plantId")
    fun getAll(plantId: Long): LiveData<List<Fertilizing>>
}