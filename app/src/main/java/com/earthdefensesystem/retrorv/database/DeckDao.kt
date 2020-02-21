package com.earthdefensesystem.retrorv.database

import androidx.room.*
import com.earthdefensesystem.retrorv.model.Cards
import com.earthdefensesystem.retrorv.model.Deck

@Dao
interface DeckDao {
    @Query("SELECT * FROM deck ORDER BY date")
    fun getDecks(): List<Deck>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeck(deck: Deck)

    @Delete
    fun deleteDeck(vararg deck: Deck)
}

