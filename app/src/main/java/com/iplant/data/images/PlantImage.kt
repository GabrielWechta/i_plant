package com.iplant.data.images
import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.android.parcel.Parcelize
import java.time.LocalDate
import java.time.LocalDateTime


@Parcelize
@Entity(tableName = "image_table")
class PlantImage (val plant_id: Long,
                  val image_date: LocalDateTime,
                  val image_name:String,
                  @PrimaryKey(autoGenerate = true) val image_id: Long = 0
) : Parcelable
