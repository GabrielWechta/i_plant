package com.iplant.data.watering

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface WateringDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(watering: Watering)
}