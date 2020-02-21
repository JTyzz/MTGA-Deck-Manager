package com.earthdefensesystem.retrorv.database

import androidx.room.Query
import androidx.room.RoomWarnings
import com.earthdefensesystem.retrorv.model.Cards

interface CardDeckDao {
    //gets all cards with deck id
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("""
                SELECT * FROM cards INNER JOIN deckCardJoin ON cards.id = deckCardJoin.cardId WHERE
        deckCardJoin.deckId = :deckId
                """)
    fun getCardsWithDeckId(deckId: Long): List<Cards>
}