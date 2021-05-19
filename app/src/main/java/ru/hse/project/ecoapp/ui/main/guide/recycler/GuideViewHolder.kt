package ru.hse.project.ecoapp.ui.main.guide.recycler

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import ru.hse.project.ecoapp.R

class GuideViewHolder(item: View) : RecyclerView.ViewHolder(item) {
    private val context = item.context
    private val view = item.findViewById<CardView>(R.id.recycler_guide_wrapper)
    private val btnView = item.findViewById<ConstraintLayout>(R.id.recycler_guide_view)
    private val image = item.findViewById<ImageView>(R.id.recycler_guide_image)
    private val text = item.findViewById<TextView>(R.id.recycler_guide_text)
    fun bind(type: Int, imageId: Int, color: Int) {
        view.setCardBackgroundColor(context.resources.getColor(color))
        text.text = context.getString(type)
        image.setImageResource(imageId)
    }

    fun setOnClickListener(clickListener: View.OnClickListener){
        btnView.setOnClickListener(clickListener)
    }

}