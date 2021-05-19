package ru.hse.project.ecoapp.data.source

import android.content.Context
import androidx.room.Room
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import ru.hse.project.backend.model.User
import ru.hse.project.ecoapp.data.source.database.AppDataBase
import ru.hse.project.ecoapp.model.PlaceMark
import kotlin.concurrent.thread

class DataSourceMemory(context: Context) {

    private val db: AppDataBase =
        Room.databaseBuilder(context, AppDataBase::class.java, "database")
            .fallbackToDestructiveMigration().build()


    fun getUser(): Task<User> {
        val task = TaskCompletionSource<User>()
        thread {
            val obj = db.userDao().getAll()
            if (obj.isEmpty()) {
                task.setException(Exception("Doesn't exist user"))
            } else {
                task.setResult(obj[0])
            }
        }
        return task.task
    }

    fun updateUser(currentUser: User): Task<Void> {
        val task = TaskCompletionSource<Void>()
        thread {
            db.userDao().update(currentUser)
            task.setResult(null)
        }
        return task.task
    }

    fun saveUser(currentUser: User) {
        thread {
            db.userDao().insert(currentUser)
        }
    }


    fun deleteUser(currentUser: User){
        thread {
            db.userDao().delete(currentUser)
        }
    }


    fun getAllFavorites(): Task<Set<PlaceMark>>{
        val task = TaskCompletionSource<Set<PlaceMark>>()
        thread {
            val obj = db.placeMarkDao().getAll()
            if (obj.isEmpty()) {
                task.setResult(setOf<PlaceMark>())
            } else {
                task.setResult(obj.toSet())
            }
        }
        return task.task
    }


    fun addFavorite(placemark: PlaceMark){
        thread {
            db.placeMarkDao().insert(placemark)
        }
    }

    fun deleteFavorite(placemark: PlaceMark){
        thread {
            db.placeMarkDao().delete(placemark)
        }
    }

}