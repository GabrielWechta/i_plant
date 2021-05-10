package com.iplant.data

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.iplant.data.fertilizing.Fertilizing
import com.iplant.data.fertilizing.FertilizingDao
import com.iplant.data.watering.Watering
import com.iplant.data.watering.WateringDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate

@Database(
    entities = [Plant::class, Watering::class, Fertilizing::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class PlantDatabase : RoomDatabase() {

    abstract fun getPlantDao(): PlantDao
    abstract fun getWateringDao(): WateringDao
    abstract fun getFertilizingDao(): FertilizingDao

    private class PlantDatabaseCallback(private val scope: CoroutineScope) :
        RoomDatabase.Callback() {
        /** this is only for development */
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(
                        database.getPlantDao(),
                        database.getWateringDao(),
                        database.getFertilizingDao()
                    )
                }
            }
        }

        /** this is only for development also */
        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(
                        database.getPlantDao(),
                        database.getWateringDao(),
                        database.getFertilizingDao()
                    )
                }
            }
        }

        suspend fun populateDatabase(
            plantDao: PlantDao,
            wateringDao: WateringDao,
            fertilizingDao: FertilizingDao
        ) {
            // Add sample plant.
            val plant = Plant(
                id = 1,
                caressing_name = "Adam",
                common_name = "Aloes",
                scientific_name = "Aloe Vera",
                adding_date = LocalDate.now(),
                light = "a lot",
                watering_period = 14,
                fertilizing_period = 30,
                death_date = LocalDate.now(),
                death_cause = "drug overdose"
            )
            plantDao.insert(plant)

            // Add sample watering.
            val watering = Watering(1, LocalDate.now(), watering_id = 1)
            wateringDao.insert(watering)

            // Add sample fertilizing.
            val fertilizing = Fertilizing(1, LocalDate.now(), fertilizing_id = 1)
            fertilizingDao.insert(fertilizing)
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