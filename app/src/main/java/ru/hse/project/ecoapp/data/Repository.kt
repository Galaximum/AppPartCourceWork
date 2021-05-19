package ru.hse.project.ecoapp.data

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import com.yandex.mapkit.geometry.Point
import ru.hse.project.backend.model.User
import ru.hse.project.ecoapp.data.source.DataSourceMemory
import ru.hse.project.ecoapp.data.source.DataSourceService
import ru.hse.project.ecoapp.model.LastPosition
import ru.hse.project.ecoapp.model.OtherUserRating
import ru.hse.project.ecoapp.model.PlaceMark
import ru.hse.project.ecoapp.model.UserRating


class Repository(
    private val dataSourceServices: DataSourceService,
    private val dataSourceMemory: DataSourceMemory
) {
    var initUser: Task<Void>
    var initPlaceMarksResult: Task<Set<PlaceMark>>
    var user: UserWrapper? = null
    var favoritePlaceMarks: FavoriteWrapper? = null
    var filteredData: Set<PlaceMark>? = null


    init {


        val taskGetUserFromMemory = dataSourceMemory.getUser()
        val taskGetFavoriteFromMemory = dataSourceMemory.getAllFavorites()
        val taskGetAllMarkers = TaskCompletionSource<Set<PlaceMark>>()

        taskGetUserFromMemory.addOnSuccessListener {
            user = UserWrapper(this, it)

            // Если юзер есть то загружаем метки из памяти + сервис
            // Иначе у нас регистрация или авторизация
            taskGetFavoriteFromMemory.addOnSuccessListener { favoriteMarkers ->
                favoritePlaceMarks = FavoriteWrapper(this, favoriteMarkers)
                dataSourceServices.trashService.getAllTrashcans().addOnSuccessListener { allMarks ->
                    val allMarkers = HashSet<PlaceMark>()
                    allMarkers.addAll(favoriteMarkers)
                    allMarkers.addAll(allMarks)
                    taskGetAllMarkers.setResult(allMarkers)
                }.addOnFailureListener {
                    val allMarkers = HashSet<PlaceMark>()
                    allMarkers.addAll(favoriteMarkers)
                    taskGetAllMarkers.setResult(allMarkers)
                }
            }
        }

        initUser = Tasks.whenAll(taskGetUserFromMemory, taskGetFavoriteFromMemory)
        initPlaceMarksResult = taskGetAllMarkers.task
        taskGetAllMarkers.task.addOnSuccessListener {
            filteredData = it
        }
    }


    fun signIn(email: String, password: String): Task<User> {
        val result = dataSourceServices.userService.signIn(email, password)
        result.addOnSuccessListener { user ->
            setLoggedInUser(user)
            // добавляем метки на карту из favorite и обычных
            val taskAllMarkers = dataSourceServices.trashService.getAllTrashcans()
            val taskFavoriteMarkers = dataSourceServices.trashService.getFavoriteTrashcans(user.id)
            val taskUnionMarkers = TaskCompletionSource<Set<PlaceMark>>()

            Tasks.whenAll(taskAllMarkers, taskFavoriteMarkers).addOnSuccessListener {
                val allMarkers = taskAllMarkers.result
                val favoriteMarkers = taskFavoriteMarkers.result
                favoritePlaceMarks = FavoriteWrapper(this, favoriteMarkers)

                for (ma in favoriteMarkers) {
                    dataSourceMemory.addFavorite(ma)
                }

                val unionCollection = hashSetOf<PlaceMark>()
                unionCollection.addAll(favoriteMarkers)
                unionCollection.addAll(allMarkers)
                taskUnionMarkers.setResult(unionCollection)
                filteredData = unionCollection
            }.addOnFailureListener {
                taskUnionMarkers.setException(taskAllMarkers.exception!!)
            }

            initPlaceMarksResult = taskUnionMarkers.task
        }

        return result
    }

    private fun signOut() {
        user = null
    }

//    fun emailIsAvailable(email: String): Task<Void> {
//        return dataSourceServices.userService.emailIsAvailable(email)
//    }

    fun registration(
        nickName: String,
        firstName: String,
        secondName: String,
        email: String,
        password: String
    ): Task<UserWrapper> {
        val task = TaskCompletionSource<UserWrapper>()
        dataSourceServices.userService.registration(
            nickName,
            firstName,
            secondName,
            email,
            password
        ).addOnSuccessListener {
            setLoggedInUser(
                User(
                    id = it,
                    nickName = nickName,
                    firstName = firstName,
                    secondName = secondName,
                    email = email,
                    password = password,
                    urlImage = "https://storage.yandexcloud.net/imagesecoapp/user_images/${it}.jpg",
                    lastPosition = LastPosition(55.746244, 37.614951, 10F)
                )
            )
            task.setResult(user)

            initPlaceMarksResult = dataSourceServices.trashService.getAllTrashcans()
            initPlaceMarksResult.addOnSuccessListener {
                filteredData = it
            }


        }.addOnFailureListener {
            task.setException(it)
        }
        return task.task
    }

    private fun setLoggedInUser(currentUser: User) {
        this.user = UserWrapper(this, currentUser)
        dataSourceMemory.saveUser(currentUser)
    }


    fun updateMemory() {
        user!!.updateMemory()
    }


    fun getRatingUsers(startPosition: Int, endPosition: Int): Task<List<OtherUserRating>> {
        return dataSourceServices.userService.getOtherUserRating(startPosition, endPosition)
    }

    fun addNewMarker(
        point: Point,
        title: String,
        address: String,
        paper: Boolean,
        glass: Boolean,
        plastic: Boolean,
        metal: Boolean
    ): Task<Void> {
        return dataSourceServices.trashService.addNewMarker(
            user!!.id,
            point,
            title,
            address,
            paper,
            glass,
            plastic,
            metal
        )
    }


    class UserWrapper(private val repository: Repository, private val user: User) {
        val id = user.id
        var nickName = user.nickName
            private set
        val firstName = user.firstName
        val secondName = user.secondName
        var email = user.email
            private set
        var password = user.password
            private set
        var lastPosition = user.lastPosition
            private set
        val urlImage = user.urlImage

        var myRating: UserRating? = null
            private set

        init {
            repository.dataSourceServices.userService.getUserRating(id).addOnSuccessListener {
                myRating = it
            }
        }

        fun updateNickName(newNickName: String): Task<Void> {
            val task = repository.dataSourceServices.userService.updateNickName(id, newNickName)
            task.addOnSuccessListener {
                user.nickName = newNickName
                this.nickName = newNickName
                repository.dataSourceMemory.updateUser(user)
            }
            return task
        }

        fun updateEmail(newEmail: String): Task<Void> {
            val task = repository.dataSourceServices.userService.updateEmail(id, newEmail)
            task.addOnSuccessListener {
                user.email = newEmail
                this.email = newEmail
                repository.dataSourceMemory.updateUser(user)
            }

            return task
        }

        fun updatePassword(newPassword: String): Task<Void> {
            val task = repository.dataSourceServices.userService.updatePassword(id, newPassword)
            task.addOnSuccessListener {
                user.password = newPassword
                this.password = newPassword
                repository.dataSourceMemory.updateUser(user)
            }
            return task
        }

        fun updateLastPosition(newLastPosition: LastPosition) {
            this.lastPosition = newLastPosition
            user.lastPosition = newLastPosition
            repository.dataSourceMemory.updateUser(user)
        }

        fun signOut() {
            repository.signOut()
            repository.dataSourceMemory.deleteUser(user)
            if (repository.favoritePlaceMarks != null) {

                for (marker in repository.favoritePlaceMarks?.getAllFavorites()!!) {
                    repository.dataSourceMemory.deleteFavorite(marker)
                }
            }
        }

        fun deleteUser(): Task<Void> {
            val task = repository.dataSourceServices.userService.deleteUser(id)
            task.addOnSuccessListener {
                this.signOut()
            }
            return task
        }


        fun updateMemory() {
            repository.dataSourceMemory.updateUser(user)
        }

        fun updateUserRating(idTrash: String): Task<Void> {
            val task = repository.dataSourceServices.userService.increaseScore(user.id, idTrash)
            task.addOnSuccessListener {
                myRating!!.score = myRating!!.score + 1
            }
            return task
        }

    }

    class FavoriteWrapper(
        private val repository: Repository,
        private val favorite: Set<PlaceMark>
    ) {
        private val collection: HashSet<PlaceMark> =
            hashSetOf<PlaceMark>().apply { this.addAll(favorite) }


        fun getAllFavorites(): Set<PlaceMark> {
            return collection
        }

        fun addFavorite(placeMark: PlaceMark): Task<Void> {
            val task = repository.dataSourceServices.trashService.addFavoriteTrashcan(
                repository.user!!.id,
                placeMark.id
            )

            task.addOnSuccessListener {
                collection.add(placeMark)
                repository.dataSourceMemory.addFavorite(placeMark)
            }
            return task
        }

        fun deleteFavorite(placeMark: PlaceMark): Task<Void> {
            val task = repository.dataSourceServices.trashService.deleteFavoriteTrashcan(
                repository.user!!.id,
                placeMark.id
            )

            task.addOnSuccessListener {
                collection.remove(placeMark)
                repository.dataSourceMemory.deleteFavorite(placeMark)
            }
            return task
        }

    }
}