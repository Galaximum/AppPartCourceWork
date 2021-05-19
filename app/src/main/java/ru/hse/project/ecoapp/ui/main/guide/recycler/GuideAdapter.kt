package ru.hse.project.ecoapp.ui.main.guide.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.hse.project.ecoapp.R

class GuideAdapter(
    private val types: List<Int>,
    private val images: List<Int>,
    private val colors: List<Int>
) :
    RecyclerView.Adapter<GuideViewHolder>() {
    private var holderClickListener:((Int) -> Unit)?=null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuideViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val layout = inflater.inflate(R.layout.guide_recycler_view, parent, false)
        return GuideViewHolder(layout)
    }

    override fun onBindViewHolder(holder: GuideViewHolder, position: Int) {
        val type = types[position]
        val image = images[position]
        val color = colors[position]
        holder.setOnClickListener { holderClickListener?.invoke(type) }

        holder.bind(type, image, color)
    }

    fun setHolderClick(it: (Int) -> Unit) {
        holderClickListener=it
    }

    override fun getItemCount(): Int = types.size
}