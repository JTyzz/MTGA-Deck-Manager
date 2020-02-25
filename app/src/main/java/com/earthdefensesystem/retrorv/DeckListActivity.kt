package com.earthdefensesystem.retrorv


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.earthdefensesystem.retrorv.adapter.DeckListAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.earthdefensesystem.retrorv.model.Deck
import com.google.android.material.floatingactionbutton.FloatingActionButton

class DeckListActivity : AppCompatActivity() {
    private lateinit var deckListViewModel: DeckListViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deck_list)

        val decklistView = findViewById<RecyclerView>(R.id.rv_deck)
        val newDeckFab = findViewById<FloatingActionButton>(R.id.new_deck_fab)

        val adapter = DeckListAdapter(this) { deckItem: Deck -> deckItemClicked(deckItem)}
        decklistView.adapter = adapter
        decklistView.layoutManager = GridLayoutManager(this, 2)



        deckListViewModel = ViewModelProvider(this).get(DeckListViewModel::class.java)

        deckListViewModel.allLDDecks.observe(this, Observer { decks ->
            decks?.let { adapter.loadDecks(it) }
        })

        newDeckFab.setOnClickListener {

        }

//        } { _, _, position, _ ->
//            Log.e("Salami", "Tapped $position")
//            if (position == 0){
//                deckListViewModel.checkExistingName(newDeck)
//                deckListViewModel.insert(newDeck)
//                adapter.notifyDataSetChanged()
//            }


//            var count = 0
//            fun checkList() {
//                if (deckListViewModel.deckList.contains(Deck("New Deck$count"))) {
//                    count++
//                    checkList()
//                }
//            }
//            if (position == 0) {
//                checkList()
//                val newDeck = Deck("New Deck$count")
//                deckList.add(newDeck)
//                saveToDb(deckList)
//                adapter.notifyDataSetChanged()
//
//            }
    }

    private fun deckItemClicked(deckItem: Deck) {
        Toast.makeText(this, "Clicked: ${deckItem.name}", Toast.LENGTH_SHORT).show()
    }
}

