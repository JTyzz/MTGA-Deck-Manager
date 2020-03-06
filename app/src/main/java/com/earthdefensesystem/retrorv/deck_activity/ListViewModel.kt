package com.earthdefensesystem.retrorv.deck_activity

import android.app.Application
import androidx.lifecycle.*
import com.earthdefensesystem.retrorv.database.AppDatabase
import com.earthdefensesystem.retrorv.database.DeckRepo
import com.earthdefensesystem.retrorv.model.Deck
import kotlinx.coroutines.launch

class ListViewModel(application: Application) : AndroidViewModel(application) {
    //reference to repo to get data
    private val repo: DeckRepo

    //livedata updates list as it changes
    var allLDDecks: LiveData<List<Deck>>


    init {
        //get reference to deckdao from appdatabase to construct deckrepo
        val deckDao = AppDatabase.getDatabase(application, viewModelScope).deckDao()
        repo = DeckRepo(deckDao)
        allLDDecks = repo.allLDDecks
    }

    //viewmodel specific coroutine scope for threads so insert doesnt block ui
    fun insert(deck: Deck) = viewModelScope.launch {
        repo.insertDeck(deck)
    }

    //when making new deck checks for name and increments if so
    fun checkExistingName(deck: Deck) : Deck {
        val deckName = deck.name
        var deckCount = 0
        fun checkNames(){
            if (allLDDecks.value!!.contains(Deck(deckName))) {
                deckCount++
                checkNames()
            }
        }
        checkNames()
        deck.name = deckName.plus(deckCount)
        return deck
    }
}
