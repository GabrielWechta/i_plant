package com.iplant.data.images
import android.content.Context
import android.os.Environment
import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.room.*
import com.akshaykale.swipetimeline.TimelineObject
import com.iplant.R
import kotlinx.android.parcel.Parcelize
import java.io.File
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

    fun getFile(context: Context): File {
        val mediaDir = Environment.getDataDirectory().let {
            File(it, context.resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        val file = if (mediaDir != null && mediaDir.exists())
            mediaDir else context.filesDir

        return File(file,image_name)
    }
}
