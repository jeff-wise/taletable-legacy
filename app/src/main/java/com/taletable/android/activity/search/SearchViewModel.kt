
package com.taletable.android.activity.search


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel



class SearchViewModel : ViewModel()
{

    val engine : SearchEngine = SearchEngine()

    private var query : SearchQuery = SearchQuery("", "")


    private val results : MutableLiveData<SearchResultList> = MutableLiveData()

    fun results() : LiveData<SearchResultList> = this.results

    fun query() : SearchQuery = this.query

    fun search(query : SearchQuery) {
        this.query = query
        results.value = engine.results(query)
    }


    fun previousSearch() {
        results.value = engine.previousSearchResults()
    }


}