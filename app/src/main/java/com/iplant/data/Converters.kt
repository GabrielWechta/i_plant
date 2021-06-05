package com.iplant.data

import android.util.Log
import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalDateTime

class Converters
{
    @TypeConverter
    fun toDate(dateString: String?): LocalDate? {
        return if (dateString == null) {
            null
        } else {
            try {
                return   LocalDate.parse(dateString)
            }
            catch (e: Exception)
            {
                Log.println(Log.ERROR,"",e.toString())
                null
            }

        }
    }

    @TypeConverter
    fun toDateString(date: LocalDate?): String? {
        return date?.toString()
    }
    @TypeConverter
    fun toDateTime(dateString: String?): LocalDateTime? {
        return if (dateString == null) {
            null
        } else {
            try {
               return LocalDateTime.parse(dateString)
            }
            catch (e: Exception)
            {
                Log.println(Log.ERROR,"",e.toString())
                null
            }


        }
    }

    @TypeConverter
    fun toDateTimeString(date: LocalDateTime?): String? {
        return date?.toString()
    }
}