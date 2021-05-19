package ru.hse.project.backend.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull
import ru.hse.project.ecoapp.model.LastPosition


@Entity
data class User(
    @PrimaryKey
    @NotNull
    var id: String,
    var nickName: String,
    var firstName: String,
    var secondName: String,
    var email: String,
    var password: String,
    var lastPosition: LastPosition,
    var urlImage:String
) {
    companion object {
        const val CODE_ID = "id"
        const val CODE_NICKNAME = "nickName"
        const val CODE_FIRST_NAME = "firstName"
        const val CODE_SECOND_NAME = "secondName"
        const val CODE_EMAIL = "email"
        const val CODE_PASSWORD = "password"
        const val CODE_URL_IMAGE="urlImage"
        const val CODE_ID_FOR_ADD_NEW_TRASH = "idUser"
    }
}