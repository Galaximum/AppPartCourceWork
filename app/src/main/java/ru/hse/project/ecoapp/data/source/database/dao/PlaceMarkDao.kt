package ru.hse.project.ecoapp.data.source.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import ru.hse.project.ecoapp.model.PlaceMark

@Dao
interface PlaceMarkDao {
    @Query("SELECT * FROM placemark")
    fun getAll(): List<PlaceMark>

    @Insert
    fun insert(placemark: PlaceMark)

    @Delete
    fun delete(placemark: PlaceMark)

}