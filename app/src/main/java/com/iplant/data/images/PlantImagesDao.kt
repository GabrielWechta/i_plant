package com.iplant.data.images

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PlantImagesDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(image: PlantImage)

    @Query("select * from image_table where plant_id = :plantId order by image_date desc limit 1")
    fun observeLastImage(plantId: Long): LiveData<List<PlantImage>>

    @Query("select * from image_table where plant_id = :plantId order by image_date desc limit 1")
    suspend fun getLastImage(plantId: Long): List<PlantImage>

    @Query("select * from image_table where plant_id = :plantId order by image_date desc")
    fun observeAllImages(plantId: Long): LiveData<List<PlantImage>>

    @Query("select * from image_table where plant_id = :plantId order by image_date desc")
    suspend fun getAllPlantImages(plantId: Long): List<PlantImage>

}