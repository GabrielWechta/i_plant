package com.iplant.data.fertilizing

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.iplant.R
import com.iplant.data.PlantEvent
import kotlinx.android.parcel.Parcelize
import java.time.LocalDate

@Parcelize
@Entity(tableName = "fertilizing_table")
data class Fertilizing(
    val plant_id: Long,
    val fertilizing_date: LocalDate,
    @PrimaryKey(autoGenerate = true) val fertilizing_id: Long = 0
) : Parcelable, PlantEvent {
    override fun getEventDate(): LocalDate = fertilizing_date
    override fun getIcon(): Int = R.drawable.ic_fertilizer
}