package ru.hse.project.ecoapp.ui.main.map.marker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.hse.project.ecoapp.R

class MarkerTypesAdapter(private val types:List<Int>,private val colors:List<Int>) :
    RecyclerView.Adapter<MarkerTypeViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarkerTypeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val layout = inflater.inflate(R.layout.view_holder_marker_type, parent, false)
        return MarkerTypeViewHolder(layout)
    }

    override fun onBindViewHolder(holder: MarkerTypeViewHolder, position: Int) {
        val type = types[position]
        val color = colors[position]
        holder.bind(type,color)
    }

    override fun getItemCount(): Int = types.size
}