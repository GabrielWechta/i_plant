package com.iplant.data

import android.os.Parcelable
import androidx.annotation.Nullable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.time.LocalDateTime

@Parcelize
@Entity(tableName = "plant_table")
data class Plant(
    /** Specification Part */
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    var caressing_name: String,
    var common_name: String,
    @Nullable
    var scientific_name: String,
    var adding_date: LocalDateTime,

    /** Growing Part */
    var light: String, //TODO maybe enum?
    var water_amount: String,
    var watering_period: LocalDateTime,
    var fertilizing_period: LocalDateTime,

    /** Death Part */
    @Nullable
    var death_date: LocalDateTime,
    @Nullable
    var death_cause: String
) : Parcelable
