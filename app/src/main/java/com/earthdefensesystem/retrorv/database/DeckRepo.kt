package com.earthdefensesystem.retrorv.database

import androidx.lifecycle.LiveData
import com.earthdefensesystem.retrorv.model.Deck

class DeckRepo(private val deckDao: DeckDao){

    val allLDDecks: LiveData<List<Deck>> = deckDao.getLDDecks()

    suspend fun deleteAll(){
        deckDao.deleteAll()
    }
//
//    fun deleteDeck(deck: Deck) = deckDao.deleteDeck(deck)

    suspend fun insert(deck: Deck){
        deckDao.insertDeck(deck)
    }

}