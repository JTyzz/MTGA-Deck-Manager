package com.earthdefensesystem.retrorv.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.earthdefensesystem.retrorv.model.Cards
import com.earthdefensesystem.retrorv.model.Deck

@Dao
interface DeckDao {
    @Query("SELECT * FROM deck_table ORDER BY date ASC")
    fun getLDDecks(): LiveData<List<Deck>>

    @Query("SELECT * FROM deck_table ORDER BY date ASC")
    fun getDecks(): List<Deck>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeck(deck: Deck)

    @Delete
    fun deleteDeck(vararg deck: Deck)

    @Query("DELETE FROM deck_table")
    suspend fun deleteAll()
}

