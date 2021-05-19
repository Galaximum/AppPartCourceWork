package ru.hse.project.ecoapp.data.source.database.converter

import androidx.room.TypeConverter
import ru.hse.project.ecoapp.model.LastPosition

object Converters {

        @TypeConverter
        @JvmStatic
        fun fromLastPosition(lastPosition: LastPosition): String {
            return "${lastPosition.latitude},${lastPosition.longtitude},${lastPosition.zoom}"
        }

        @TypeConverter
        @JvmStatic
        fun toLastPosition(data: String): LastPosition {
            val splitData = data.split(",")
            return LastPosition(
                splitData[0].toDouble(),
                splitData[1].toDouble(),
                splitData[2].toFloat()
            )
        }
}