package ru.hse.project.ecoapp.data.source

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.yandex.mapkit.geometry.Point
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import ru.hse.project.backend.model.User
import ru.hse.project.ecoapp.model.LastPosition
import ru.hse.project.ecoapp.model.OtherUserRating
import ru.hse.project.ecoapp.model.PlaceMark
import ru.hse.project.ecoapp.model.UserRating
import ru.hse.project.ecoapp.util.Parser
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class DataSourceService() {

    val userService = UserServiceWrapper(this)
    val trashService = TrashServiceWrapper(this)

    companion object {
        private val JSON = "application/json; charset=utf-8".toMediaType()
        private const val HOST_IP = "192.168.31.121"

        // private const val HOST_IP = "192.168.1.47"
        private const val BASE_URL = "http://$HOST_IP:8080"
    }

    private val client: OkHttpClient = OkHttpClient()
    private fun call(request: Request): Task<Response> {
        val task = TaskCompletionSource<Response>()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                task.setException(e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    task.setResult(response)
                } else {
                    val obj = JSONObject(response.body?.string()!!)
                    val message = obj.getString("message")
                    task.setException(Exception(message))
                }
            }
        })
        return task.task
    }

    class UserServiceWrapper(private val dss: DataSourceService) {
        companion object {
            private const val USER_CONTROLLER = "/users"
            private const val USER_SIGN_IN = "/signInUser"
            private const val USER_REGISTER = "/registerUser"
            private const val USER_DELETE = "/deleteUser?id="
            private const val USER_UPDATE_NICK_NAME = "/updateNickName"
            private const val USER_UPDATE_EMAIL = "/updateEmail"
            private const val USER_UPDATE_PASSWORD = "/updatePassword"
            private const val USER_UPDATE_RATING = "/updateUserRating?id="
            private const val USER_GET_RATING = "/userRating?id="
            private const val USER_GET_RATING_USERS = "/usersRating"

        }


        fun signIn(email: String, password: String): Task<User> {

            val json = JSONObject().apply {
                put(User.CODE_EMAIL, email)
                put(User.CODE_PASSWORD, password)
            }.toString()

            val request: Request = Request.Builder()
                .url(BASE_URL + USER_CONTROLLER + USER_SIGN_IN)
                .post(json.toRequestBody(JSON))
                .build()

            val task = TaskCompletionSource<User>()
            dss.call(request).addOnSuccessListener {
                val obj = JSONObject(it.body!!.string())
                val id = obj.getString(User.CODE_ID)
                val nickName = obj.getString(User.CODE_NICKNAME)
                val fistName = obj.getString(User.CODE_FIRST_NAME)
                val secondName = obj.getString(User.CODE_SECOND_NAME)
                val urlImage = obj.getString(User.CODE_URL_IMAGE)
                task.setResult(
                    User(
                        id = id,
                        nickName = nickName,
                        firstName = fistName,
                        secondName = secondName,
                        email = email,
                        password = password,
                        urlImage = urlImage,
                        lastPosition = LastPosition(55.746244, 37.614951, 10F)
                    )
                )
            }.addOnFailureListener {
                task.setException(it)
            }
            return task.task
        }

        fun registration(
            nickName: String,
            firstName: String,
            secondName: String,
            email: String,
            password: String
        ): Task<String> {
            val json = JSONObject().apply {
                put(User.CODE_NICKNAME, nickName)
                put(User.CODE_FIRST_NAME, firstName)
                put(User.CODE_SECOND_NAME, secondName)
                put(User.CODE_EMAIL, email)
                put(User.CODE_PASSWORD, password)
            }.toString()

            val request: Request = Request.Builder()
                .url(BASE_URL + USER_CONTROLLER + USER_REGISTER)
                .post(json.toRequestBody(JSON))
                .build()

            val task = TaskCompletionSource<String>()
            dss.call(request).addOnSuccessListener {
                task.setResult(it.body!!.string())
            }.addOnFailureListener {
                task.setException(it)
            }
            return task.task
        }


        fun updateNickName(id: String, newNickName: String): Task<Void> {
            val json = JSONObject().apply {
                put(User.CODE_ID, id)
                put(User.CODE_NICKNAME, newNickName)
            }.toString()

            val request: Request = Request.Builder()
                .url(BASE_URL + USER_CONTROLLER + USER_UPDATE_NICK_NAME)
                .patch(json.toRequestBody(JSON))
                .build()

            val task = TaskCompletionSource<Void>()
            dss.call(request).addOnSuccessListener {
                task.setResult(null)
            }.addOnFailureListener {
                task.setException(it)
            }
            return task.task
        }

        fun updateEmail(id: String, newEmail: String): Task<Void> {
            val json = JSONObject().apply {
                put(User.CODE_ID, id)
                put(User.CODE_EMAIL, newEmail)
            }.toString()

            val request: Request = Request.Builder()
                .url(BASE_URL + USER_CONTROLLER + USER_UPDATE_EMAIL)
                .patch(json.toRequestBody(JSON))
                .build()

            val task = TaskCompletionSource<Void>()
            dss.call(request).addOnSuccessListener {
                task.setResult(null)
            }.addOnFailureListener {
                task.setException(it)
            }
            return task.task

        }

        fun updatePassword(id: String, newPassword: String): Task<Void> {
            val json = JSONObject().apply {
                put(User.CODE_ID, id)
                put(User.CODE_PASSWORD, newPassword)
            }.toString()


            val request: Request = Request.Builder()
                .url(BASE_URL + USER_CONTROLLER + USER_UPDATE_PASSWORD)
                .patch(json.toRequestBody(JSON))
                .build()

            val task = TaskCompletionSource<Void>()
            dss.call(request).addOnSuccessListener {
                task.setResult(null)
            }.addOnFailureListener {
                task.setException(it)
            }
            return task.task
        }


        fun deleteUser(id: String): Task<Void> {
            val request: Request = Request.Builder()
                .url(BASE_URL + USER_CONTROLLER + USER_DELETE + id)
                .delete()
                .build()

            val task = TaskCompletionSource<Void>()
            dss.call(request).addOnSuccessListener {
                task.setResult(null)
            }.addOnFailureListener {
                task.setException(it)
            }
            return task.task
        }

        fun emailIsAvailable(email: String): Task<Void> {
            val request: Request = Request.Builder()
                .url("$BASE_URL$USER_CONTROLLER/$email")
                .get()
                .build()
            val task = TaskCompletionSource<Void>()
            dss.call(request).addOnSuccessListener {
                task.setResult(null)
            }.addOnFailureListener {
                task.setException(it)
            }
            return task.task
        }


        fun getOtherUserRating(start: Int, end: Int): Task<List<OtherUserRating>> {

            val json = JSONObject().apply {
                put("startPosition", start)
                put("endPosition", end)
            }.toString()

            val request: Request = Request.Builder()
                .url(BASE_URL + USER_CONTROLLER + USER_GET_RATING_USERS)
                .post(json.toRequestBody(JSON))
                .build()
            val task = TaskCompletionSource<List<OtherUserRating>>()
            dss.call(request).addOnSuccessListener {
                val obj = Parser.parseUsersRating(JSONArray(it.body!!.string()))
                task.setResult(obj)
            }.addOnFailureListener {
                task.setException(it)
            }
            return task.task
        }

        fun getUserRating(id: String): Task<UserRating> {
            val request: Request = Request.Builder()
                .url(BASE_URL + USER_CONTROLLER + USER_GET_RATING + id)
                .get()
                .build()
            val task = TaskCompletionSource<UserRating>()
            dss.call(request).addOnSuccessListener {
                val obj = JSONObject(it.body!!.string())
                task.setResult(
                    UserRating(
                        score = obj.getInt(UserRating.CODE_SCORE),
                        position = obj.getInt(UserRating.CODE_POSITION)
                    )
                )
            }.addOnFailureListener {
                task.setException(it)
            }
            return task.task
        }

        fun increaseScore(id: String, idTrash: String): Task<Void> {
            val json = JSONObject().apply {
                put(User.CODE_ID, id)
                put(PlaceMark.CODE_ID_FOR_SERVICE, idTrash)
            }.toString()

            val request: Request = Request.Builder()
                .url(BASE_URL + USER_CONTROLLER + USER_UPDATE_RATING)
                .post(json.toRequestBody(JSON))
                .build()

            val task = TaskCompletionSource<Void>()
            dss.call(request).addOnSuccessListener {
                task.setResult(null)
            }.addOnFailureListener {
                task.setException(it)
            }
            return task.task
        }

    }

    class TrashServiceWrapper(private val dss: DataSourceService) {
        companion object {
            private const val TRASH_CONTROLLER = "/trashcans"
            private const val TRASH_GET_ALL_TRASHCANS = "/allTrash"
            private const val TRASH_GET_FAVORITE_TRASHCANS = "/favorite?id="
            private const val TRASH_ADD_FAVORITE_TRASHCAN = "/addFavorite"
            private const val TRASH_DELETE_FAVORITE_TRASHCAN = "/deleteFavorite"
            private const val TRASH_ADD_NEW = "/addNewTrash"

        }

        fun getAllTrashcans(): Task<Set<PlaceMark>> {

            val request: Request = Request.Builder()
                .url(BASE_URL + TRASH_CONTROLLER + TRASH_GET_ALL_TRASHCANS)
                .get()
                .build()

            val task = TaskCompletionSource<Set<PlaceMark>>()
            dss.call(request).addOnSuccessListener {
                val obj = Parser.parsePlaceMarks(JSONArray(it.body!!.string()), false)

                task.setResult(obj)
            }.addOnFailureListener {
                task.setException(it)
            }
            return task.task


        }


        fun getFavoriteTrashcans(userId: String): Task<Set<PlaceMark>> {
            val request: Request = Request.Builder()
                .url(BASE_URL + TRASH_CONTROLLER + TRASH_GET_FAVORITE_TRASHCANS + userId)
                .get()
                .build()

            val task = TaskCompletionSource<Set<PlaceMark>>()
            dss.call(request).addOnSuccessListener {
                val obj = Parser.parsePlaceMarks(JSONArray(it.body!!.string()), true)
                task.setResult(obj)
            }.addOnFailureListener {
                task.setException(it)
            }
            return task.task
        }

        fun addFavoriteTrashcan(userId: String, trashcanId: String): Task<Void> {
            val json = JSONObject().apply {
                put(User.CODE_ID, userId)
                put(PlaceMark.CODE_ID_FOR_SERVICE, trashcanId)
            }.toString()

            val request: Request = Request.Builder()
                .url(BASE_URL + TRASH_CONTROLLER + TRASH_ADD_FAVORITE_TRASHCAN)
                .post(json.toRequestBody(JSON))
                .build()

            val task = TaskCompletionSource<Void>()
            dss.call(request).addOnSuccessListener {
                task.setResult(null)
            }.addOnFailureListener {
                task.setException(it)
            }
            return task.task
        }

        fun deleteFavoriteTrashcan(userId: String, trashcanId: String): Task<Void> {
            val json = JSONObject().apply {
                put(User.CODE_ID, userId)
                put(PlaceMark.CODE_ID_FOR_SERVICE, trashcanId)
            }.toString()

            val request: Request = Request.Builder()
                .url(BASE_URL + TRASH_CONTROLLER + TRASH_DELETE_FAVORITE_TRASHCAN)
                .delete(json.toRequestBody(JSON))
                .build()

            val task = TaskCompletionSource<Void>()
            dss.call(request).addOnSuccessListener {
                task.setResult(null)
            }.addOnFailureListener {
                task.setException(it)
            }
            return task.task
        }

        fun addNewMarker(
            id: String,
            point: Point,
            title: String,
            address: String,
            paper: Boolean,
            glass: Boolean,
            plastic: Boolean,
            metal: Boolean
        ): Task<Void> {
            val json = JSONObject().apply {
                put(User.CODE_ID_FOR_ADD_NEW_TRASH, id)
                put(PlaceMark.CODE_LATITUDE, point.latitude)
                put(PlaceMark.CODE_LONGITUDE, point.longitude)
                put(PlaceMark.CODE_TITLE, title)
                put(PlaceMark.CODE_ADDRESS, address)
                put(PlaceMark.CODE_PAPER, paper)
                put(PlaceMark.CODE_GLASS, glass)
                put(PlaceMark.CODE_PLASTIC, plastic)
                put(PlaceMark.CODE_METAL, metal)
            }.toString()

            val request: Request = Request.Builder()
                .url(BASE_URL + TRASH_CONTROLLER + TRASH_ADD_NEW)
                .post(json.toRequestBody(JSON))
                .build()

            val task = TaskCompletionSource<Void>()
            dss.call(request).addOnSuccessListener {
                task.setResult(null)
            }.addOnFailureListener {
                task.setException(it)
            }
            return task.task
        }

    }
}