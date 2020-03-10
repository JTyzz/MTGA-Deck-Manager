package com.earthdefensesystem.retrorv.deck_activity

import android.app.Application
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.lifecycle.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.earthdefensesystem.retrorv.database.AppDatabase
import com.earthdefensesystem.retrorv.database.DeckRepo
import com.earthdefensesystem.retrorv.model.*
import com.earthdefensesystem.retrorv.network.ApiFactory
import com.earthdefensesystem.retrorv.network.SearchRepo
import com.earthdefensesystem.retrorv.utilities.ImageStoreManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class SearchViewModel(application: Application) : AndroidViewModel(application) {
    private val searchRepo: SearchRepo = SearchRepo(ApiFactory.apiService)
    private val repo: DeckRepo
    private val context = application.applicationContext

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
    var openDeckCard: LiveData<DecksWithCards>? = null


    init {
        //get reference to deckdao from appdatabase to construct deckrepo
        val deckDao = AppDatabase.getDatabase(application, viewModelScope).deckDao()
        repo = DeckRepo(deckDao)
        allLDDecks = repo.allLDDecks
        deckNamesLD = repo.deckNames
    }

    fun getCardsByDeckId(deckId: Long) {
        scope.launch {
            openDeckCard = repo.getDeckById(deckId)
//                openDeckCard.postValue(repoDeck)
//                deckCardsLiveData.postValue(openDeckCard.value?.cards)
        }
    }

    //viewmodel specific coroutine scope for threads so insert doesnt block ui
    fun insertDeck(deck: Deck) = viewModelScope.launch {
        repo.insertDeck(deck)
    }

    fun insertCardtoDeck(card: Card, deckId: Long, count: Int) = viewModelScope.launch {
        val cardCount = CardCount(card.cardId, count, card)
        Log.d("salami", "${cardCount.card.name} inserted")
        repo.insertCardCount(cardCount)
        val junction = DeckCardJoin(cardCount.id, deckId)
        Log.d("salami", "${cardCount.card.name} inserted into $deckId")
        repo.insertRelation(junction)
    }

    fun updateCardCount(oldCard: CardCount, count: Int) = viewModelScope.launch{
        val newCardCount = CardCount(oldCard.id, count, oldCard.card)
        repo.insertCardCount(newCardCount)
    }

    fun updateDeckBackground(deck: Deck, cardCount: CardCount) = viewModelScope.launch {
        Log.d("salami", "${deck.name} updating background")
        Glide.with(context)
            .asBitmap()
            .load(cardCount.card.imageUris?.artCrop)
            .into(object : CustomTarget<Bitmap>(){
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    ImageStoreManager.saveToInternalStorage(context, resource,cardCount.card.cardId)
                    Log.d("salami", "Picture added successfully")
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })
        repo.updateDeckBackground(deck.deckId!!, cardCount.card.cardId)

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

    fun <T> MutableLiveData <T>.notifyObserver(){
        this.value = this.value
    }
}
