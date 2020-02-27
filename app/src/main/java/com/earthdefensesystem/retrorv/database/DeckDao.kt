package com.earthdefensesystem.retrorv.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.earthdefensesystem.retrorv.model.Cards
import com.earthdefensesystem.retrorv.model.Deck
import com.earthdefensesystem.retrorv.model.DeckCardJoin
import com.earthdefensesystem.retrorv.model.DecksWithCards

@Dao
interface DeckDao {
    @Query("SELECT * FROM deck_table ORDER BY date ASC")
    fun getLDDecks(): LiveData<List<Deck>>

    @Query("SELECT * FROM deck_table ORDER BY date ASC")
    fun getDecks(): List<Deck>

    @Transaction
    @Query("SELECT * FROM deck_table")
    fun getDecksWithCards(): List<DecksWithCards>

    @Transaction
    @Query("SELECT * FROM deck_table WHERE deckId = :deckId")
    fun getDeckWithCardsById(deckId: Long): DecksWithCards

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeck(deck: Deck)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(cards: Cards)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCardList(list: List<Cards>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRelation(deckCardJoin: DeckCardJoin)

    @Delete
    fun deleteDeck(vararg deck: Deck)

    @Query("DELETE FROM deck_table")
    suspend fun deleteAll()
}

