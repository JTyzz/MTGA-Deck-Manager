package com.jtyzzer.vantress.database

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.jtyzzer.vantress.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Deck::class, CardCount::class, DeckCardJoin::class],
    version = 1, exportSchema = false
)
@TypeConverters(StringListConverter::class)
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
            return INSTANCE
                ?: synchronized(this) {
                    val instance = Room.databaseBuilder(
                        context.applicationContext, AppDatabase::class.java, "app_database"
                    )
                        .addCallback(
                            DeckDatabaseCallback(
                                scope
                            )
                        )
                        .build()
                    INSTANCE = instance
                    instance
                }
        }

        //places New Deck as first entry when db is created
        private class DeckDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(
                            database.deckDao()
                        )
                    }
                }
            }
        }

        suspend fun populateDatabase(deckDao: DeckDao) {
            deckDao.deleteAll()
            deckDao.insertDeck(Deck("Sample Deck", 1, 1))
        }
    }
}