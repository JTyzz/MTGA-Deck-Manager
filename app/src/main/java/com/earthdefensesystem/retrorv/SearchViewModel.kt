package com.earthdefensesystem.retrorv

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.earthdefensesystem.retrorv.database.DeckRepo
import com.earthdefensesystem.retrorv.model.Cards
import com.earthdefensesystem.retrorv.rest.ApiFactory
import com.earthdefensesystem.retrorv.rest.SearchRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class SearchViewModel (application: Application) : AndroidViewModel(application){
    private val repo: SearchRepo = SearchRepo(ApiFactory.apiService)

    private val parentJob = Job()

    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Default

    private val scope = CoroutineScope(coroutineContext)

    val searchCardsLiveData = MutableLiveData<List<Cards>>()

    fun getCardsSearch(color: String){
        scope.launch {
            val searchCards = repo.getSearchCards(color)
            searchCardsLiveData.postValue(searchCards)
        }
    }
}