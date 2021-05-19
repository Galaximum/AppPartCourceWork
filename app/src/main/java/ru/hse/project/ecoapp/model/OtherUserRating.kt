package ru.hse.project.ecoapp.model

data class OtherUserRating(
    val nickName:String,
    val imageUrl:String,
    //Удалить
    val score:Int,
    val position:Int
){
    companion object{
        const val CODE_NICK_NAME="nickName"
        const val CODE_IMAGE_URL="image"
        const val CODE_SCORE="score"
        const val CODE_POSITION="position"
    }
}
