package ru.hse.project.ecoapp.model

import android.text.Spannable

data class SearchModel(
    val id:String,
    var isFavorite:Boolean,
    var address: Spannable,
    var latitude:Double,
    var longitude:Double
)
