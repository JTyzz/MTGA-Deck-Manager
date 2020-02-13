package com.earthdefensesystem.retrorv

import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.GridView
import com.earthdefensesystem.retrorv.R
import com.earthdefensesystem.retrorv.adapter.DeckListAdapter
import com.earthdefensesystem.retrorv.model.Cards
import com.earthdefensesystem.retrorv.model.Deck
import io.paperdb.Paper

class DeckListActivity : AppCompatActivity() {
    var deckList = ArrayList<Deck>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deck_list)
        Paper.init(this)
        initDeck()

        val decklist_view = findViewById<GridView>(R.id.gv_deck)

        val adapter = DeckListAdapter(deckList, this@DeckListActivity)
        decklist_view?.adapter = adapter

        decklist_view.setOnItemClickListener { _, _, position, _ ->
            Log.e("Salami", "Tapped $position")
            var count = 0
            fun checkList() {
                if (deckList.contains(Deck("New Deck$count"))) {
                    count++
                    checkList()
                }
            }
            if (position == 0) {
                checkList()
                val newDeck = Deck("New Deck$count")
                deckList.add(newDeck)
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun initDeck() {
        Thread {
            deckList = Paper.book().read("decks", ArrayList())
        }
        if (deckList.size == 0) {
            val startDeck = Deck("Add new deck")
            deckList.add(startDeck)
            Thread {
                Paper.book().write("decks", deckList)
            }
        }
    }

}
