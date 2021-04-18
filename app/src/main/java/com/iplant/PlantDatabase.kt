package com.iplant

import android.content.Context
import androidx.room.*

@Database(entities = [Plant::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class PlantDatabase : RoomDatabase() {

    abstract fun getPlantDao(): PlantDao

    @Volatile
    private var INSTANCE: PlantDatabase? = null

    fun getDatabase(context: Context): PlantDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                PlantDatabase::class.java,
                "plant_database"
            ).build()
            INSTANCE = instance
            instance
        }
    }
}