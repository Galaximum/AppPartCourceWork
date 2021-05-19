package ru.hse.project.ecoapp.model

data class FilterModel(
    var isChecked:Boolean=false,
    val isWhat:TrashTypes,
    val color:Int
)