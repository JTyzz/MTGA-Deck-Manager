package com.earthdefensesystem.retrorv.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.earthdefensesystem.retrorv.model.Deck
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Deck::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun deckDao(): DeckDao

    companion object {

        // For Singleton instantiation
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, AppDatabase::class.java, "app_database"
                )
                    .addCallback(DeckDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        //places New Deck as first entry when db is created
        private class DeckDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.deckDao())
                    }
                }
            }
        }
        suspend fun populateDatabase( deckDao: DeckDao){
            deckDao.deleteAll()
            var deck = Deck("New Deck")
            deckDao.insertDeck(deck)
            deck = Deck("New Deck2")
            deckDao.insertDeck(deck)
            deck = Deck("New Deck3")
            deckDao.insertDeck(deck)
        }
    }
}