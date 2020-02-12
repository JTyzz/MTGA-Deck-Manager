package com.earthdefensesystem.retrorv

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.GridView
import com.earthdefensesystem.retrorv.R
import com.earthdefensesystem.retrorv.adapter.DeckListAdapter
import com.earthdefensesystem.retrorv.model.Cards
import com.earthdefensesystem.retrorv.model.Deck

class DeckListActivity : AppCompatActivity() {

    var decklist_view: GridView? = null
    var deckList: ArrayList<Deck> = ArrayList()
    var adapter: DeckListAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deck_list)

        decklist_view = findViewById(R.id.gv_deck)
        addSampleCards()
        adapter = DeckListAdapter(deckList, this)
        decklist_view?.adapter = adapter

    }

    private fun addSampleCards(){
        val card1 = Deck()
        card1.name = "Fire"
        deckList.add(card1)

        val card2 = Deck()
        card1.name = "Water"
        deckList.add(card2)

        val card3 = Deck()
        card1.name = "Ice"
        deckList.add(card3)

        val card4 = Deck()
        card1.name = "Air"
        deckList.add(card4)

        val card5 = Deck()
        card1.name = "Cars"
        deckList.add(card5)


    }
}
