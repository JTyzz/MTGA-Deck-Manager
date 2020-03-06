package com.earthdefensesystem.retrorv.database

import androidx.lifecycle.LiveData
import com.earthdefensesystem.retrorv.model.*

class DeckRepo(private val deckDao: DeckDao) {

    val allLDDecks: LiveData<List<Deck>> = deckDao.getLDDecks()
    val deckNames: LiveData<List<String>> = deckDao.getNames()

    //gets deck object with a list<Cards> by decks id
    fun getDeckById(deckId: Long): DecksWithCards {
        return deckDao.getDeckWithCardsById(deckId)
    }

    suspend fun insertCardList(list: List<Card>) {
        deckDao.insertCardList(list)
    }

    suspend fun insertDeck(deck: Deck) {
        deckDao.insertDeck(deck)
    }

    suspend fun insertCard(cards: Card) {
        deckDao.insertCard(cards)
    }

    suspend fun insertRelation(relation: DeckCardJoin){
        deckDao.insertRelation(relation)
    }

    fun deleteDeck(deck: Deck) = deckDao.deleteDeck(deck)

    suspend fun deleteAll() {
        deckDao.deleteAll()
    }


}