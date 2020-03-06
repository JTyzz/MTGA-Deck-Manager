package com.earthdefensesystem.retrorv.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.earthdefensesystem.retrorv.model.*

@Dao
interface DeckDao {
    @Query("SELECT * FROM deck_table ORDER BY date ASC")
    fun getLDDecks(): LiveData<List<Deck>>

    @Query("SELECT * FROM deck_table ORDER BY date ASC")
    fun getDecks(): List<Deck>

    @Query("SELECT name from deck_table")
    fun getNames(): LiveData<List<String>>

    @Transaction
    @Query("SELECT * FROM deck_table")
    fun getDecksWithCards(): List<DecksWithCards>

    @Transaction
    @Query("SELECT * FROM deck_table WHERE deckId = :deckId")
    fun getDeckWithCardsById(deckId: Long): DecksWithCards

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeck(deck: Deck)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(cards: Card)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCardList(list: List<Card>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRelation(deckCardJoin: DeckCardJoin)

    @Delete
    fun deleteDeck(vararg deck: Deck)

    @Query("DELETE FROM deck_table")
    suspend fun deleteAll()
}

