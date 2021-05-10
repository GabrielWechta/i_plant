package com.iplant.data.watering

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.time.LocalDate

@Parcelize
@Entity(tableName = "watering_table")
data class Watering(
    val plant_id: Long,
    val watering_date: LocalDate,
    @PrimaryKey(autoGenerate = true) val watering_id: Long = 0
) : Parcelable
