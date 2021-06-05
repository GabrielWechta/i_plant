package com.iplant.MediaAPI

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContract
import com.google.gson.*
import com.iplant.data.Converters
import com.iplant.data.Plant
import com.iplant.data.fertilizing.Fertilizing
import com.iplant.data.watering.Watering
import java.io.InputStream
import java.io.OutputStream
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class JSONParser(context: Context) {
    val gson:Gson = GsonBuilder()
        .registerTypeAdapter(LocalDate::class.java, DateAdap())

        .create()
    val context = context
    internal class CreateDocument : ActivityResultContract<String, ActivityResult>() {
        override fun createIntent(context: Context, fileName: String?): Intent {
            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_TITLE, fileName);
            return intent
        }

        override fun parseResult(resultCode: Int, intent: Intent?): ActivityResult? =
             ActivityResult(resultCode, intent)
    }

    internal class ReadDocument : ActivityResultContract<Any?, ActivityResult>() {
        override fun createIntent(context: Context, input: Any?): Intent {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            return intent
        }
        override fun parseResult(resultCode: Int, intent: Intent?): ActivityResult? =
            ActivityResult(resultCode, intent)
    }

    fun writeToJSON(uri: Uri, plant: DataModel)
    {
        val outputStream: OutputStream? = context.getContentResolver().openOutputStream(uri)
        var js:String? = null;
        if (outputStream != null) {
            js = gson.toJson(plant)
            outputStream.write(js.toByteArray())
            outputStream.close()
        }
    }
    class  DateAdap: JsonDeserializer<LocalDate>, JsonSerializer<LocalDate>
    {
        override fun serialize(
            src: LocalDate, typeOfSrc: Type?,
            context: JsonSerializationContext?
        ): JsonElement {
            val obj = JsonObject()
            obj.addProperty("time", src.toString())
            return obj
        }
        override fun deserialize(
            json: JsonElement?,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): LocalDate {
            if (json != null) {
             val lon:String =   json.asJsonObject.get("time").asString
                val   df = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                return  LocalDate.parse(lon, df);


            }
            return LocalDate.now()

        }

    }


 fun readFromJSON(uri: Uri): DataModel?
 {
     val inputStream: InputStream? = context.getContentResolver().openInputStream(uri)
     var plantData:DataModel? = null
     if (inputStream != null) {
         plantData =  gson.fromJson(inputStream.reader(), DataModel::class.java)
         inputStream.close()
     }
    return plantData;
 }

    fun generateName(plant: Plant): String? {
        val localDate: LocalDateTime = LocalDateTime.now()
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss-SSS")
        val date: String = localDate.format(formatter)
        val name = plant.caressing_name +"_"+date+".json"
        return name
    }
}

class DataModel(plant: Plant?, watering: Watering?, fertilizing: Fertilizing?)
{
    val plant = plant
    val watering = watering
    val fertilizing = fertilizing

}