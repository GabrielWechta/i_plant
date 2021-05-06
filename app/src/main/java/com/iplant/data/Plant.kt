package com.iplant.data

import android.os.Parcelable
import androidx.annotation.Nullable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.time.LocalDate
import java.time.LocalDateTime

@Parcelize
@Entity(tableName = "plant_table")
data class Plant(
    /** Specification Part */
    val caressing_name: String,
    val common_name: String,
    @Nullable val scientific_name: String?,
    val adding_date: LocalDate,

    /** Growing Part */
    val light: String, //TODO maybe enum?
    val watering_period: Int,
    val fertilizing_period: Int,
    @Nullable val notes: String? = null,

    /** Death Part */
    @Nullable val death_date: LocalDate? = null,
    @Nullable val death_cause: String? = null,

    @PrimaryKey(autoGenerate = true) val id: Long = 0
) : Parcelable
