package ru.hse.project.ecoapp.ui.main.profile.rating

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import ru.hse.project.ecoapp.App
import ru.hse.project.ecoapp.R
import ru.hse.project.ecoapp.model.OtherUserRating

class RatingViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
    private val image: ImageView = view.findViewById(R.id.view_holder_rating_image)
    private val nickName: TextView = view.findViewById(R.id.view_holder_rating_nick_name)
    private val position: TextView = view.findViewById(R.id.view_holder_rating_position)
    private val backView: ConstraintLayout = view.findViewById(R.id.view_holder_rating_back)
    private val cardView: CardView = view.findViewById(R.id.view_holder_rating_cardview)
    fun bind(item: OtherUserRating) {
        App.getComponent().getPicassoHttps().load(item.imageUrl).fit().centerCrop()
            .error(R.drawable.place_holder_user).into(image)
        nickName.text = item.nickName
        position.text = (item.position + 1).toString()
        if (item.position % 2 != 0) {
            backView.setBackgroundColor(view.context.resources.getColor(R.color.default_gray_filter))
            cardView.elevation = 0F
        }

    }

}