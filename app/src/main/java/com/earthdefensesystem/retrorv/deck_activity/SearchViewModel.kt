package com.earthdefensesystem.retrorv.deck_activity

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.earthdefensesystem.retrorv.database.AppDatabase
import com.earthdefensesystem.retrorv.database.DeckRepo
import com.earthdefensesystem.retrorv.model.Card
import com.earthdefensesystem.retrorv.model.CardCount
import com.earthdefensesystem.retrorv.model.Deck
import com.earthdefensesystem.retrorv.model.DeckCardJoin
import com.earthdefensesystem.retrorv.network.ApiFactory
import com.earthdefensesystem.retrorv.network.SearchRepo
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

    //search fragment
    val searchCardsLiveData = MutableLiveData<List<Card>>()

    //list fragment
    var deckNamesLD: LiveData<List<String>>
    var deckNames: MutableList<String> = mutableListOf()
    var allLDDecks: LiveData<List<Deck>>

    //deck fragment
    val deckCardsLiveData = MutableLiveData<List<CardCount>>()
    val openDeck = MutableLiveData<Deck>()


    init {
        //get reference to deckdao from appdatabase to construct deckrepo
        val deckDao = AppDatabase.getDatabase(application, viewModelScope).deckDao()
        repo = DeckRepo(deckDao)
        allLDDecks = repo.allLDDecks
        deckNamesLD = repo.deckNames
    }

    fun getCardsByDeckId(deckId: Long) {
        scope.launch {
            val listCards = repo.getDeckById(deckId)
                openDeck.postValue(listCards.deck)
                deckCardsLiveData.postValue(listCards.cards)
        }
    }

    //viewmodel specific coroutine scope for threads so insert doesnt block ui
    fun insertDeck(deck: Deck) = viewModelScope.launch {
        repo.insertDeck(deck)
    }

    fun insertCardtoDeck(card: Card, deckId: Long, count: Int) = viewModelScope.launch {
        val cardCount = CardCount(card.id, count, card)
        repo.insertCard(card)
        repo.insertCardCount(cardCount)
        val junction = DeckCardJoin(cardCount.id, deckId)
        repo.insertRelation(junction)
    }

    fun changeCardCount(oldCard: CardCount, count: Int) = viewModelScope.launch{
        val newCardCount = CardCount(oldCard.id, count, oldCard.card)
        repo.insertCardCount(newCardCount)
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


    fun setDeck(deck: Deck) {
        openDeck.value = deck
    }
}
