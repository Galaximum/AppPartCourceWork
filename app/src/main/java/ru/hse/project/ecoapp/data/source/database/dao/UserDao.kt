package ru.hse.project.ecoapp.data.source.database.dao

import androidx.room.*
import ru.hse.project.backend.model.User


@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getAll(): List<User>

    @Insert
    fun insert(employee: User)

    @Update
    fun update(employee: User)

    @Delete
    fun delete(employee: User)
}