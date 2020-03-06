package com.earthdefensesystem.retrorv.deck_activity

import android.app.Application
import androidx.lifecycle.*
import com.earthdefensesystem.retrorv.database.AppDatabase
import com.earthdefensesystem.retrorv.database.DeckRepo
import com.earthdefensesystem.retrorv.model.Card
import com.earthdefensesystem.retrorv.model.Cards
import com.earthdefensesystem.retrorv.model.Deck
import com.earthdefensesystem.retrorv.rest.ApiFactory
import com.earthdefensesystem.retrorv.rest.SearchRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class SearchViewModel(application: Application) : AndroidViewModel(application) {
    private val searchRepo: SearchRepo = SearchRepo(ApiFactory.apiService)
    private val repo: DeckRepo

    private val parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Default
    private val scope = CoroutineScope(coroutineContext)

    val searchCardsLiveData = MutableLiveData<List<Card>>()
    val deckCardsLiveData = MutableLiveData<List<Cards>>()
    var allLDDecks: LiveData<List<Deck>>
    val allCards = MutableLiveData<List<Cards>>()
    val openDeck = MutableLiveData<Deck>()


    init {
        //get reference to deckdao from appdatabase to construct deckrepo
        val deckDao = AppDatabase.getDatabase(application, viewModelScope).deckDao()
        repo = DeckRepo(deckDao)
        allLDDecks = repo.allLDDecks
    }

    fun getCardsByDeckId(deckId: Long) {
        val listCards = repo.getDeckById(deckId).cards
        deckCardsLiveData.postValue(listCards)
    }

    //viewmodel specific coroutine scope for threads so insert doesnt block ui
    fun insert(deck: Deck) = viewModelScope.launch {
        repo.insertDeck(deck)
    }

    fun getCardsSearch(color: String) {
        scope.launch {
            val searchCards = searchRepo.getSearchCards(color)
            searchCards?.removeIf { it.imageUris?.small == null }
            searchCardsLiveData.postValue(searchCards)
        }
    }

    fun checkExistingName(deck: Deck): Deck {
        val deckName = deck.name
        var deckCount = 0
        fun checkNames() {
            if (allLDDecks.value!!.contains(Deck(deckName))) {
                deckCount++
                checkNames()
            }
        }
        checkNames()
        if (deckCount != 0) {
            deck.name = deckName.plus(deckCount)
        }

        return deck
    }

    fun setDeck(deck: Deck) {
        openDeck.value = deck
    }
}
