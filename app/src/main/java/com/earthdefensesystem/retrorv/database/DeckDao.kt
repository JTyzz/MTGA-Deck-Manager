package com.earthdefensesystem.retrorv.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
    @Query("SELECT * FROM deck_table WHERE deck_id = :deckId")
    fun getDeckWithCardsById(deckId: Long): LiveData<DecksWithCards>

    @Query("SELECT deck_id FROM deck_table WHERE name = :deckName")
    suspend fun getNewDeckId(deckName: String): Long

    @Query("UPDATE deck_table SET uri = :imageUri where deck_id = :deckId")
    suspend fun updateDeckBackground(deckId: Long, imageUri: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeck(deck: Deck)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCardCount(cardCount: CardCount)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRelation(deckCardJoin: DeckCardJoin)

    @Delete
    fun deleteDeck(vararg deck: Deck)


    @Query("DELETE FROM deck_table where deck_id = :deckId")
    suspend fun deleteDeckById(deckId: Long)

    @Query("DELETE FROM deck_table")
    suspend fun deleteAll()
}

