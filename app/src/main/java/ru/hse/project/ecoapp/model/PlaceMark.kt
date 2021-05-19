package ru.hse.project.ecoapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull

@Entity
data class PlaceMark(
    @PrimaryKey
    @NotNull
    var id:String,
    var address: String,
    var image: String,
    var title: String,
    var latitude: Double,
    var longitude: Double,
    var isGlass: Boolean,
    var isPaper:Boolean,
    var isPlastic:Boolean,
    var isMetal:Boolean,
    var isFavorite:Boolean
) {

    companion object {
        const val CODE_ID_FOR_SERVICE="trashCanId"
        const val CODE_ID = "id"
        const val CODE_ADDRESS = "address"
        const val CODE_TITLE = "title"
        const val CODE_IMAGE = "image"
        const val CODE_LATITUDE = "latitude"
        const val CODE_LONGITUDE = "longitude"
        const val CODE_GLASS = "glass"
        const val CODE_PAPER = "paper"
        const val CODE_PLASTIC = "plastic"
        const val CODE_METAL = "metal"
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PlaceMark

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }


}
