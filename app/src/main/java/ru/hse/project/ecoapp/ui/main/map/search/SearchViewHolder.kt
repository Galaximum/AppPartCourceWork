package ru.hse.project.ecoapp.ui.main.map.search

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.daimajia.swipe.SwipeLayout
import ru.hse.project.ecoapp.R
import ru.hse.project.ecoapp.model.SearchModel

class SearchViewHolder(item: View) : RecyclerView.ViewHolder(item) {
    private val context = item.context
    val swipeLayout: SwipeLayout = itemView.findViewById(R.id.swipe)
    private var clickListener: ((Double, Double) -> Unit)? = null
    private var clickHiddenBtnListener: ((String) -> Unit)? = null
    private val text = itemView.findViewById<TextView>(R.id.search_view_text)
    private val view = itemView.findViewById<ConstraintLayout>(R.id.search_view_clicable)
    private val hiddenLay = itemView.findViewById<ConstraintLayout>(R.id.search_view_bottom_wrapper)
    private val hiddenBtn = itemView.findViewById<ConstraintLayout>(R.id.view_holder_search_hidden_btn)
    fun bind(newObject: SearchModel) {
        text.text = newObject.address
        view.setOnClickListener {
            clickListener?.invoke(newObject.latitude, newObject.longitude)
        }

        val idColor =
            if (newObject.isFavorite) R.color.color_swipe_lay_delete_favorite else R.color.color_swipe_lay_add_favorite

        hiddenLay.setBackgroundColor(context.resources.getColor(idColor))
        hiddenBtn.setOnClickListener {
            clickHiddenBtnListener?.invoke(newObject.id)
        }

    }

    fun setClickListener(it: ((Double, Double) -> Unit)?) {
        clickListener = it
    }

    fun setOnClickHiddenBtnListener(it: ((String) -> Unit)?) {
        clickHiddenBtnListener = it
    }


}