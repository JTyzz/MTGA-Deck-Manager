package com.earthdefensesystem.retrorv


import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Slide
import android.transition.TransitionManager
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.earthdefensesystem.retrorv.adapter.DeckListAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.earthdefensesystem.retrorv.model.Deck
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_deck_list.*

class DeckListActivity : AppCompatActivity() {
    private lateinit var deckListViewModel: DeckListViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deck_list)

        val decklistView = findViewById<RecyclerView>(R.id.rv_deck)
        val newDeckFab = findViewById<FloatingActionButton>(R.id.new_deck_fab)

        val adapter = DeckListAdapter(this) { deckItem: Deck -> deckItemClicked(deckItem) }
        decklistView.adapter = adapter
        decklistView.layoutManager = GridLayoutManager(this, 2)

        deckListViewModel = ViewModelProvider(this).get(DeckListViewModel::class.java)

        deckListViewModel.allLDDecks.observe(this, Observer { decks ->
            decks?.let { adapter.loadDecks(it) }
        })

        newDeckFab.setOnClickListener {
            val inflater: LayoutInflater =
                getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            // inflate popup view
            val view = inflater.inflate(R.layout.new_deck_popup, null)
            val et = view.findViewById<EditText>(R.id.deck_name_et)
            val buttonPopup = view.findViewById<Button>(R.id.button_popup)

            // new popupwindow instance
            val popupWindow = PopupWindow(
                view, // Custom view to show in popup window
                LinearLayout.LayoutParams.WRAP_CONTENT, // Width of popup window
                LinearLayout.LayoutParams.WRAP_CONTENT // Window height
            )

            popupWindow.elevation = 10.0F
            popupWindow.isFocusable = true

            val slideIn = Slide()
            slideIn.slideEdge = Gravity.TOP
            popupWindow.enterTransition = slideIn

            val slideOut = Slide()
            slideOut.slideEdge = Gravity.END
            popupWindow.exitTransition = slideOut

            //onclick listener
            buttonPopup.setOnClickListener {
                val word = et.text.toString()
                val time = System.currentTimeMillis()
                val deck = Deck(word, time)
                deckListViewModel.insert(deck)
                deckListViewModel.checkExistingName(deck)
                popupWindow.dismiss()
            }

            // dismiss listener
            popupWindow.setOnDismissListener {
                adapter.notifyDataSetChanged()
            }


            //display popup
            TransitionManager.beginDelayedTransition(deck_list_layout)
            popupWindow.showAtLocation(
                deck_list_layout, Gravity.CENTER, 0, 0
            )
        }
    }

    private fun deckItemClicked(deckItem: Deck) {
        Toast.makeText(this, "Clicked: ${deckItem.name}", Toast.LENGTH_SHORT).show()
    }
}

