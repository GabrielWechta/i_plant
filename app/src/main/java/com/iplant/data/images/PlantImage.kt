package com.iplant.data.images
import android.content.Context
import android.os.Environment
import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.room.*
import com.iplant.R
import kotlinx.android.parcel.Parcelize
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime


@Parcelize
@Entity(tableName = "image_table")
class PlantImage (val plant_id: Long,
                  val image_date: LocalDateTime,
                  val image_name:String,
                  @PrimaryKey(autoGenerate = true) val image_id: Long = 0
) : Parcelable {

    fun getFile(context: Context): File {
        val mediaDir = Environment.getDataDirectory().let {
            File(it, context.resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        val file = if (mediaDir != null && mediaDir.exists())
            mediaDir else context.filesDir

        return File(file,image_name)
    }
}
