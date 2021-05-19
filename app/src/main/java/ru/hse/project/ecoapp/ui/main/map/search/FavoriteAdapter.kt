package ru.hse.project.ecoapp.ui.main.map.search

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.daimajia.swipe.SwipeLayout
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter
import com.google.android.gms.tasks.Task
import ru.hse.project.ecoapp.R
import ru.hse.project.ecoapp.model.PlaceMark

class FavoriteAdapter() : RecyclerSwipeAdapter<SearchViewHolder>(), Filterable {
    private val filter: SearchFilter =
        SearchFilter(hashSetOf<PlaceMark>()) { notifyDataSetChanged() }
    private var clickListener: ((Double, Double) -> Unit)? = null
    private var clickHiddenBtnListener: ((String) -> Task<Void>)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val layout = inflater.inflate(R.layout.view_holder_element_search, parent, false)
        return SearchViewHolder(layout)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val item = filter.filteredData.elementAt(position)
        holder.bind(item)
        holder.setClickListener(clickListener)

        holder.setOnClickHiddenBtnListener {
            clickHiddenBtnListener!!.invoke(it).addOnSuccessListener {
                filter.filteredData.remove(item)
                notifyDataSetChanged()
            }

            holder.swipeLayout.showMode = SwipeLayout.ShowMode.PullOut
        }

        holder.swipeLayout.showMode = SwipeLayout.ShowMode.PullOut
        holder.swipeLayout.addDrag(
            SwipeLayout.DragEdge.Right,
            holder.swipeLayout.findViewById(R.id.search_view_bottom_wrapper)
        )
    }

    override fun getItemCount(): Int = filter.filteredData.size

    override fun getSwipeLayoutResourceId(position: Int): Int = R.id.swipe

    override fun getFilter(): Filter {
        return filter
    }

    fun setData(it: Set<PlaceMark>) {
        filter.setData(it)
    }

    fun setContext(it: Context) {
        filter.setContext(it)
    }

    fun setClickListener(it: (Double, Double) -> Unit) {
        clickListener = it
    }

    fun setOnClickHiddenBtnListener(it: (String) -> Task<Void>) {
        clickHiddenBtnListener = it
    }
}