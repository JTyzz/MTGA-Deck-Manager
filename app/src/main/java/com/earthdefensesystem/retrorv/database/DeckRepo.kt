package com.earthdefensesystem.retrorv.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.earthdefensesystem.retrorv.model.*

class DeckRepo(private val deckDao: DeckDao) {

    val allLDDecks: LiveData<List<DeckWithCards>> = deckDao.getLDDecks()
    val deckNames: LiveData<List<String>> = deckDao.getNames()

    //gets deck object with a list<Cards> by decks id
    suspend fun getDeckById(deckId: Long): LiveData<DeckWithCards> {
        return deckDao.getDeckWithCardsById(deckId)
    }

    //inserts new deck and returns its id
    suspend fun setNewDeck(deck: Deck): Long {
        return deckDao.getNewDeckId(deck)
    }

    suspend fun insertCardCount(cardCount: CardCount){
        deckDao.insertCardCount(cardCount)
    }

    suspend fun insertRelation(relation: DeckCardJoin){
        deckDao.insertRelation(relation)
    }

    suspend fun updateDeckBackground(deckId: Long, imageUri: String){
        deckDao.updateDeckBackground(deckId, imageUri)
    }

    suspend fun updateDeckColorId(deckId: Long, colorId: String){
        deckDao.updateDeckColorIdentity(deckId, colorId)
    }

    suspend fun updateDeckName(deckId: Long, name: String){
        deckDao.updateDeckName(deckId, name)
    }

    suspend fun deleteCard(cardId: String) = deckDao.deleteCard(cardId)

    fun deleteDeck(deck: Deck) = deckDao.deleteDeck(deck)

    suspend fun deleteDeckById(id: Long) = deckDao.deleteDeckById(id)

    suspend fun deleteAll() {
        deckDao.deleteAll()
    }


}