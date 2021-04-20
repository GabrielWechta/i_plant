package com.iplant.data

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@Database(entities = [Plant::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class PlantDatabase : RoomDatabase() {

    abstract fun getPlantDao(): PlantDao

    private class PlantDatabaseCallback(private val scope: CoroutineScope) :
        RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.getPlantDao())
                }
            }
        }

        suspend fun populateDatabase(plantDao: PlantDao) {
            // Delete all content here.
//            plantDao.deleteAll()

            // Add sample words.
            val plant = Plant(
                id = 1,
                caressing_name = "Adam",
                common_name = "Aloes",
                scientific_name = "Aloe Vera",
                adding_date = LocalDateTime.now(),
                light = "a lot",
                water_amount = "not much",
                watering_period = LocalDateTime.now(),
                fertilizing_period = LocalDateTime.now(),
                death_date = LocalDateTime.now(),
                death_cause = "drug overdose"
            )
            plantDao.insert(plant)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: PlantDatabase? = null
        fun getDatabase(context: Context, scope: CoroutineScope): PlantDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PlantDatabase::class.java,
                    "plant_database"
                ).addCallback(
                    PlantDatabaseCallback(
                        scope
                    )
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}