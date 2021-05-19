package ru.hse.project.ecoapp.ui.main.profile.rating

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.hse.project.ecoapp.R
import ru.hse.project.ecoapp.model.OtherUserRating

class RatingAdapter(private val data:List<OtherUserRating>):RecyclerView.Adapter<RatingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatingViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val layout = inflater.inflate(R.layout.view_holder_element_rating, parent, false)
        return RatingViewHolder(layout)
    }

    override fun onBindViewHolder(holder: RatingViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = data.size

}