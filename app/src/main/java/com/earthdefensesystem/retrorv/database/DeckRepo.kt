package com.earthdefensesystem.retrorv.database

import com.earthdefensesystem.retrorv.model.Deck

class DeckRepo private constructor(private val deckDao: DeckDao){

    val allDecks: List<Deck> = deckDao.getDecks()

    fun deleteDeck(deck: Deck) = deckDao.deleteDeck(deck)

    suspend fun insert(deck: Deck){
        deckDao.insertDeck(deck)
    }

}