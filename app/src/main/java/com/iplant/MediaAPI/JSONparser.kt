package com.iplant.MediaAPI

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContract
import com.google.gson.Gson
import com.iplant.data.Plant
import com.iplant.data.fertilizing.Fertilizing
import com.iplant.data.watering.Watering
import java.io.FileWriter
import java.io.OutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class JSONParser(context: Context) {
    val gson:Gson = Gson()
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

    fun writeToJSON(uri: Uri, plant:DataModel)
    {
        val outputStream: OutputStream? = context.getContentResolver().openOutputStream(uri)
        if (outputStream != null) {
            outputStream.write(gson.toJson(plant).toByteArray())
            outputStream.close()
        }


    }


   // fun readFromJSON(filaPath:String): DataModel
  //  {

  //  }

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