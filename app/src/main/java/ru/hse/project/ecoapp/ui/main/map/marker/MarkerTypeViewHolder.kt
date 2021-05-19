package ru.hse.project.ecoapp.ui.main.map.marker

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import ru.hse.project.ecoapp.R

class MarkerTypeViewHolder(item: View) : RecyclerView.ViewHolder(item) {
    private val context = item.context
    private val view = item.findViewById<ConstraintLayout>(R.id.back_view_marker_types)
    private val text = item.findViewById<TextView>(R.id.text_marker_type)
    fun bind(type: Int, color: Int) {
        view.setBackgroundColor(context.resources.getColor(color))
        text.text = context.getString(type)
    }

}