package com.iplant.data.images
import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.room.*
import com.akshaykale.swipetimeline.TimelineObject
import kotlinx.android.parcel.Parcelize
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter


@Parcelize
@Entity(tableName = "image_table")
class PlantImage (val plant_id: Long,
                  val image_date: LocalDateTime,
                  val image_name:String,
                  @PrimaryKey(autoGenerate = true) val image_id: Long = 0
) : Parcelable, TimelineObject {
    override fun getTimestamp(): Long {
        return image_date.toEpochSecond(ZoneOffset.UTC) * 1000
    }

    override fun getTitle(): String {
        return image_date.format(DateTimeFormatter.ofPattern("HH:mm"))
    }

    override fun getImageUrl(): String {
        return image_name
    }

}
