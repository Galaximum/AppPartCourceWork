package ru.hse.project.ecoapp.model

data class UserRating(
   var score:Int,
    val position:Int
){
    companion object{
        const val  CODE_SCORE="score"
        const val  CODE_POSITION="position"
    }
}
