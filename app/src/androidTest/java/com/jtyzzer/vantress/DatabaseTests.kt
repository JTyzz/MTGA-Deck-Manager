package com.jtyzzer.vantress

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.jtyzzer.vantress.database.AppDatabase
import com.jtyzzer.vantress.database.DeckDao
import com.jtyzzer.vantress.model.Card
import com.jtyzzer.vantress.model.CardCount
import com.jtyzzer.vantress.model.Deck
import com.jtyzzer.vantress.model.DeckCardJoin
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
        val card = Card(null,null,"1", null, "Card 1" ,
            null,null,null,null,null,null)
        val cardCount = CardCount("1a", 2, card)
        runBlocking {
            deckDao.insertCardCount(cardCount)
        }
        val junction = DeckCardJoin("1", 1)
        runBlocking {
            deckDao.insertRelation(junction)
        }
        val byDeck = deckDao.getDecksWithCards()
        assertEquals(byDeck.value?.get(0)!!.cards[0], "Card 1")
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
        val card = Card(null,null,"1", null, "Card 2" ,
            null,null,null,null,null,null)
        val cardCount = CardCount("1", 2, card)
        runBlocking {
            deckDao.insertCardCount(cardCount)
        }
        val junction = DeckCardJoin("1", deckId)
        runBlocking {
            deckDao.insertRelation(junction)
        }
        val byDeckId = deckDao.getDeckWithCardsById(deckId)
        assertEquals(byDeckId.value!!.cards[0].card.name, "Card 2")
    }
}