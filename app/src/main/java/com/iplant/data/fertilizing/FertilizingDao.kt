package com.iplant.data.fertilizing

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface FertilizingDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(fertilizing: Fertilizing)
}