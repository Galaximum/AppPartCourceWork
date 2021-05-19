package ru.hse.project.ecoapp.ui.main.map.search

import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import ru.hse.project.ecoapp.R
import ru.hse.project.ecoapp.model.PlaceMark
import ru.hse.project.ecoapp.model.SearchModel
import ru.hse.project.ecoapp.util.Porter


class SearchFilter(private var data: Set<PlaceMark>, private val notify: () -> Unit) :
    android.widget.Filter() {
    private var context: Context? = null
    var filteredData: HashSet<SearchModel> = hashSetOf<SearchModel>()

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        val results = FilterResults()
        if (constraint == null || constraint.isEmpty()) {

            if (data.size > COUNT_OF_SHOWED_ELEMENTS) {
                val newData = data.map { x ->
                    SearchModel(
                        x.id, x.isFavorite,
                        SpannableString(x.address).apply {
                            this.setSpan(
                                Color.BLACK,
                                0,
                                x.address.length,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                        },
                        x.latitude, x.longitude
                    )
                }.toList().slice(0 until COUNT_OF_SHOWED_ELEMENTS).toHashSet()
                results.values = newData
                results.count = newData.size
            } else {
                results.values = data.map { x ->
                    SearchModel(
                        x.id, x.isFavorite,
                        SpannableString(x.address).apply {
                            this.setSpan(
                                Color.BLACK,
                                0,
                                x.address.length,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                        },
                        x.latitude, x.longitude
                    )
                }.toHashSet()
                results.count = data.size
            }


        } else {

            val words = constraint
                .replace("\"(\\[\"]|.*)?\"".toRegex(), " ")
                .split("[^\\p{Alpha}]+".toRegex())
                .filter { it.isNotBlank() }
                .map(Porter::stem).toList()
            var newData = mutableListOf<Pair<Int, SearchModel>>()
            for (marker in data) {
                val spannableString = checkText(marker.address.toString().toLowerCase(), words)
                if (spannableString.first != 0) {
                    newData.add(
                        Pair(
                            spannableString.first,
                            SearchModel(
                                marker.id,
                                marker.isFavorite,
                                spannableString.second,
                                marker.latitude,
                                marker.longitude
                            )
                        )
                    )
                }
            }
            newData.sortByDescending { x -> x.first }
            if (newData.size > COUNT_OF_SHOWED_ELEMENTS) {
                newData = newData.slice(0 until COUNT_OF_SHOWED_ELEMENTS).toMutableList()
            }
            results.values = newData.map { x -> x.second }.toHashSet()
            results.count = newData.size
        }

        return results
    }


    override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
        //it set the data from filter to adapter list and refresh the recyclerview adapter
        filteredData = results!!.values as HashSet<SearchModel>
        notify.invoke()
    }

    fun setData(data: Set<PlaceMark>) {
        this.data = data

        if(data.size> COUNT_OF_SHOWED_ELEMENTS){
            filteredData = data.map { x ->
                SearchModel(
                    x.id, x.isFavorite,
                    SpannableString(x.address).apply {
                        this.setSpan(
                            Color.BLACK,
                            0,
                            x.address.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    },
                    x.latitude, x.longitude
                )
            }.slice(0 until COUNT_OF_SHOWED_ELEMENTS).toHashSet()
        }else {
            filteredData = data.map { x ->
                SearchModel(
                    x.id, x.isFavorite,
                    SpannableString(x.address).apply {
                        this.setSpan(
                            Color.BLACK,
                            0,
                            x.address.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    },
                    x.latitude, x.longitude
                )
            }.toHashSet()
        }
    }

    fun setContext(it: Context) {
        context = it
    }

    private fun checkText(address: String, words: List<String>): Pair<Int, Spannable> {
        val listOfWordsAddress = address.replace("\"(\\[\"]|.*)?\"".toRegex(), " ")
            .split("[^\\p{Alpha}]+".toRegex())
            .filter { it.isNotBlank() }.toList()

        val wordToSpan: Spannable = SpannableString(address)


        wordToSpan.setSpan(
            context!!.resources.getColor(R.color.color_text_second_priority),
            0,
            address.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        var counter = 0
        for (word in listOfWordsAddress) {
            for (searchWord in words) {
                if (word.startsWith(searchWord)) {
                    val startIndex = address.indexOf(word)
                    val endIndex = startIndex + word.length
                    wordToSpan.setSpan(
                        ForegroundColorSpan(Color.BLACK),
                        startIndex,
                        endIndex,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    counter += 1
                }
            }
        }

        return Pair(counter, wordToSpan)
    }

    companion object {
        const val COUNT_OF_SHOWED_ELEMENTS = 30
    }
}