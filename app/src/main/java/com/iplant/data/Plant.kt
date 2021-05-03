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

    val caressing_name: String,
    val common_name: String,
    @Nullable val scientific_name: String,
    val adding_date: LocalDateTime,

    /** Growing Part */
    val light: String, //TODO maybe enum?
    val water_amount: String,
    val watering_period: Int,
    val fertilizing_period: Int,

    /** Death Part */
    @Nullable val death_date: LocalDateTime,
    @Nullable val death_cause: String,
    @PrimaryKey(autoGenerate = true) val id: Long = 0
) : Parcelable
