package ru.hse.project.ecoapp.util

import org.json.JSONArray
import ru.hse.project.ecoapp.model.OtherUserRating
import ru.hse.project.ecoapp.model.PlaceMark

class Parser {

    companion object {

        fun parsePlaceMarks(json: JSONArray,isFavorite:Boolean): Set<PlaceMark> {
            val placeMarks = hashSetOf<PlaceMark>()
            if (json.length() != 0) {

                for (i in 0 until json.length()) {
                    val obj = json.getJSONObject(i)
                    val placeMark = PlaceMark(
                        id = obj.getString(PlaceMark.CODE_ID),
                        address = obj.getString(PlaceMark.CODE_ADDRESS),
                        title = obj.getString(PlaceMark.CODE_TITLE),
                        latitude =  obj.getDouble(PlaceMark.CODE_LATITUDE),
                        longitude =  obj.getDouble(PlaceMark.CODE_LONGITUDE),
                        image = obj.getString(PlaceMark.CODE_IMAGE),
                        isPaper = obj.getBoolean(PlaceMark.CODE_PAPER),
                        isGlass = obj.getBoolean(PlaceMark.CODE_GLASS),
                        isPlastic = obj.getBoolean(PlaceMark.CODE_PLASTIC),
                        isMetal = obj.getBoolean(PlaceMark.CODE_METAL),
                        isFavorite = isFavorite
                    )
                    placeMarks.add(placeMark)
                }
            }
            return placeMarks
        }

        fun parseUsersRating(json: JSONArray): List<OtherUserRating> {
            val usersRating = arrayListOf<OtherUserRating>()
            if (json.length() != 0) {
                for (i in 0 until json.length()) {
                    val obj = json.getJSONObject(i)
                    val userRating = OtherUserRating(
                        nickName = obj.getString(OtherUserRating.CODE_NICK_NAME),
                        imageUrl = obj.getString(OtherUserRating.CODE_IMAGE_URL),
                        score = obj.getInt(OtherUserRating.CODE_SCORE),
                        position = obj.getInt(OtherUserRating.CODE_POSITION)
                    )
                    usersRating.add(userRating)
                }
            }
            return usersRating
        }

    }


}