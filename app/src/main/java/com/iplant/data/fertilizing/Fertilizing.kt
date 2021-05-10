package com.iplant.data.fertilizing

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.time.LocalDate

@Parcelize
@Entity(tableName = "fertilizing_table")
data class Fertilizing(
    val plant_id: Long,
    val fertilizing_date: LocalDate,
    @PrimaryKey(autoGenerate = true) val fertilizing_id: Long = 0
) : Parcelable