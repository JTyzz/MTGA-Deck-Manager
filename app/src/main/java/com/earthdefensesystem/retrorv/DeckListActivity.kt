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

class DeckListActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deck_list)

        val decklist_view = findViewById<GridView>(R.id.gv_deck)

        val deckList = ArrayList<Deck>()

        val card1 = Deck("Fire")
        deckList.add(card1)

        val card2 = Deck("Water")
        deckList.add(card2)

        val card3 = Deck("Ice")
        deckList.add(card3)

        val card4 = Deck("Air")
        deckList.add(card4)

        val card5 = Deck("Cars")
        deckList.add(card5)

        val adapter = DeckListAdapter(deckList, this@DeckListActivity)
        decklist_view?.adapter = adapter
    }
}
