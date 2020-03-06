package com.earthdefensesystem.retrorv.deck_activity

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.earthdefensesystem.retrorv.database.AppDatabase
import com.earthdefensesystem.retrorv.database.DeckRepo
import com.earthdefensesystem.retrorv.model.Card
import com.earthdefensesystem.retrorv.model.Deck
import com.earthdefensesystem.retrorv.model.DeckCardJoin
import com.earthdefensesystem.retrorv.rest.ApiFactory
import com.earthdefensesystem.retrorv.rest.SearchRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*
import kotlin.coroutines.CoroutineContext

class SearchViewModel(application: Application) : AndroidViewModel(application) {
    private val searchRepo: SearchRepo = SearchRepo(ApiFactory.apiService)
    private val repo: DeckRepo

    private val parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Default
    private val scope = CoroutineScope(coroutineContext)

    //search fragment
    val searchCardsLiveData = MutableLiveData<List<Card>>()
    val deckCardsLiveData = MutableLiveData<List<Card>>()

    //list fragment
    var deckNamesLD: LiveData<List<String>>
    var deckNames: MutableList<String> = mutableListOf()
    var allLDDecks: LiveData<List<Deck>>

    //deck fragment
    val allCards = MutableLiveData<List<Card>>()
    val openDeck = MutableLiveData<Deck>()


    init {
        //get reference to deckdao from appdatabase to construct deckrepo
        val deckDao = AppDatabase.getDatabase(application, viewModelScope).deckDao()
        repo = DeckRepo(deckDao)
        allLDDecks = repo.allLDDecks
        deckNamesLD = repo.deckNames
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

    //checks name of deck and increments name by 1 if it exists
    fun checkExistingName(deck: Deck): Deck {
        var deckName = deck.name
        Log.d("salami", "initial $deckName")
        var count = 0
        fun checkNames() {
            if (deckNames.contains(deckName)) {
                count++
//                if (deckNames.value!!.contains(deckName.plus(count))) {
                if (deckNames.contains(deckName.plus(count))) {
                    Log.d("salami", count.toString())
                    checkNames()
                }
            }
        }
        checkNames()
        if (count != 0) {
            deckName = deckName.plus(count)
            deck.name = deckName
            Log.d("salami", "changed ${deck.name}")
        }
        return deck
    }

    fun addCardtoDeck(card: Card, deckId: Long, count: Int) = viewModelScope.launch {
        card.count = count
        repo.insertCard(card)
        val junction = DeckCardJoin(card.id!!, deckId)
        repo.insertRelation(junction)
    }

    fun setDeck(deck: Deck) {
        openDeck.value = deck
    }
}
