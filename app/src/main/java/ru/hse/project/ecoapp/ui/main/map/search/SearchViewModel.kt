package ru.hse.project.ecoapp.ui.main.map.search

import android.content.Context
import android.widget.Filterable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter
import com.google.android.gms.tasks.Task
import ru.hse.project.ecoapp.data.Repository

class SearchViewModel(private val repository: Repository) : ViewModel() {
    private var adapterAllSearch: AllSearchAdapter = AllSearchAdapter()
    private var adapterFavoriteSearch: FavoriteAdapter = FavoriteAdapter()
    private var activeAdapter: RecyclerSwipeAdapter<*> = adapterAllSearch

    private val _changePage = MutableLiveData<RecyclerSwipeAdapter<*>>()
    val changePage: LiveData<RecyclerSwipeAdapter<*>> = _changePage


    fun changePage() {
        activeAdapter = if (activeAdapter == adapterAllSearch) {
            adapterFavoriteSearch
        } else {
            adapterAllSearch
        }
        if (activeAdapter == adapterFavoriteSearch) {
            bottomViewStateExpanded()
        }
        _changePage.value = activeAdapter
    }


    fun bottomViewStateExpanded() {
        adapterAllSearch.setData(repository.filteredData!!)
        adapterAllSearch.notifyDataSetChanged()
        adapterFavoriteSearch.setData(repository.filteredData!!.filter { x->x.isFavorite }.toSet())
        adapterFavoriteSearch.notifyDataSetChanged()
    }

    fun changeSearchText(text: String?) {
        (activeAdapter as Filterable).filter.filter(text)
    }

    fun getActiveAdapter(): RecyclerSwipeAdapter<*> {
        return activeAdapter
    }

    fun setClickListener(it: (Double, Double) -> Unit) {
        adapterAllSearch.setOnClickListener(it)
        adapterFavoriteSearch.setClickListener(it)
    }

    fun setContext(it: Context) {
        adapterAllSearch.setContext(it)
        adapterFavoriteSearch.setContext(it)
    }

    fun setOnClickHiddenBtnListener(it: (String) -> Task<Void>) {
        adapterAllSearch.setOnClickHiddenBtnListener(it)
        adapterFavoriteSearch.setOnClickHiddenBtnListener(it)
    }
}