package com.earthdefensesystem.retrorv.database

import androidx.lifecycle.LiveData
import com.earthdefensesystem.retrorv.model.Cards
import com.earthdefensesystem.retrorv.model.Deck
import com.earthdefensesystem.retrorv.model.DeckCardJoin
import com.earthdefensesystem.retrorv.model.DecksWithCards

class DeckRepo(private val deckDao: DeckDao) {

    val allLDDecks: LiveData<List<Deck>> = deckDao.getLDDecks()

    //gets deck object with a list<Cards> by decks id
    fun getDeckById(deckId: Long): DecksWithCards {
        return deckDao.getDeckWithCardsById(deckId)
    }

    suspend fun insertCardList(list: List<Cards>) {
        deckDao.insertCardList(list)
    }

    suspend fun insertDeck(deck: Deck) {
        deckDao.insertDeck(deck)
    }

    suspend fun insertCard(cards: Cards) {
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