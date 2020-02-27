package com.earthdefensesystem.retrorv

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.earthdefensesystem.retrorv.database.AppDatabase
import com.earthdefensesystem.retrorv.database.DeckDao
import com.earthdefensesystem.retrorv.model.Cards
import com.earthdefensesystem.retrorv.model.Deck
import com.earthdefensesystem.retrorv.model.DeckCardJoin
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import org.junit.Before
import java.io.IOException

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4ClassRunner::class)
class DatabaseTests {
    private lateinit var deckDao: DeckDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java).build()
        deckDao = db.deckDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun checkDecksWithCards() {
        val deck = Deck("New Deck", 0)
        runBlocking {
            deckDao.insertDeck(deck)
        }
        val card = Cards("1", "Card 1")
        runBlocking {
            deckDao.insertCard(card)
        }
        val junction = DeckCardJoin("1", 1)
        runBlocking {
            deckDao.insertRelation(junction)
        }
        val byDeck = deckDao.getDecksWithCards()
        assertEquals(byDeck[0].cards[0].name, "Card 1")
    }

    @Test
    @Throws(Exception::class)
    fun checkDecksWithCardsById() {
        val deckId: Long = 2
        val deck = Deck("New Deck", 0, 1)
        runBlocking {
            deckDao.insertDeck(deck)
        }
        val deck2 = Deck("New Deck2", 0, deckId)
        runBlocking {
            deckDao.insertDeck(deck2)
        }
        val card = Cards("1", "Card 2")
        runBlocking {
            deckDao.insertCard(card)
        }
        val junction = DeckCardJoin("1", deckId)
        runBlocking {
            deckDao.insertRelation(junction)
        }
        val byDeckId = deckDao.getDeckWithCardsById(deckId)
        assertEquals(byDeckId.cards[0].name, "Card 2")
    }
}