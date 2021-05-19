package ru.hse.project.ecoapp.data.source.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.hse.project.backend.model.User
import ru.hse.project.ecoapp.data.source.database.converter.Converters
import ru.hse.project.ecoapp.data.source.database.dao.PlaceMarkDao
import ru.hse.project.ecoapp.data.source.database.dao.UserDao
import ru.hse.project.ecoapp.model.PlaceMark


@Database(entities = [User::class, PlaceMark::class], version = 13)
@TypeConverters(Converters::class)
abstract class AppDataBase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun placeMarkDao(): PlaceMarkDao
}