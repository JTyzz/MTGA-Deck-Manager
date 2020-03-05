package com.earthdefensesystem.retrorv.deck_activity

import android.app.Application
import androidx.lifecycle.*
import com.earthdefensesystem.retrorv.database.AppDatabase
import com.earthdefensesystem.retrorv.database.DeckRepo
import com.earthdefensesystem.retrorv.model.Card
import com.earthdefensesystem.retrorv.model.Deck
import com.earthdefensesystem.retrorv.rest.ApiFactory
import com.earthdefensesystem.retrorv.rest.SearchRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class DeckViewModel (application: Application) : AndroidViewModel(application) {
    private val repo: SearchRepo = SearchRepo(ApiFactory.apiService)

    private val parentJob = Job()

    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Default

    private val scope = CoroutineScope(coroutineContext)

    val searchCardsLiveData = MutableLiveData<List<Card>>()

    fun getCardsSearch(color: String){
        scope.launch {
            val searchCards = repo.getSearchCards(color)
            searchCards?.removeIf{ it.imageUris?.small == null}
            searchCardsLiveData.postValue(searchCards)
        }
    }
}
