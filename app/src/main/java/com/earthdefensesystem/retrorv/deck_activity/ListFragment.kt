package com.earthdefensesystem.retrorv.deck_activity

import android.content.Context
import android.os.Bundle
import android.transition.Slide
import android.transition.TransitionManager
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.earthdefensesystem.retrorv.R
import com.earthdefensesystem.retrorv.adapter.ListAdapter
import com.earthdefensesystem.retrorv.model.Card
import com.earthdefensesystem.retrorv.model.Deck
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ListFragment : Fragment() {


    companion object {
        fun newInstance() = ListFragment()
    }
    private lateinit var viewModel: SearchViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(SearchViewModel::class.java)
        val view = inflater.inflate(R.layout.list_fragment, container, false)

        val fab = view.findViewById<FloatingActionButton>(R.id.deck_fab)
        val recyclerView = view.findViewById<RecyclerView>(R.id.list_rv)
        val activity = activity as Context





        val listAdapter = ListAdapter(activity) { deckItem: Deck -> listItemClicked(deckItem) }
        recyclerView.adapter = listAdapter
        recyclerView.layoutManager = GridLayoutManager(activity, 2) as RecyclerView.LayoutManager?

        viewModel.allLDDecks.observe(this, Observer { decks ->
            decks?.let { listAdapter.loadDecks(it)
            listAdapter.notifyDataSetChanged()}
        })


        fab.setOnClickListener {
            makeAlertDialog()
        }
        return view
    }
    private fun listItemClicked(deckItem: Deck){
        Log.d("salami", deckItem.name)
        viewModel.setDeck(deckItem)
        Log.d("salami", viewModel.openDeck.value?.name
        )
        toDeckFragment()
    }

    private fun makeAlertDialog(){
        // inflate popup view
        val view = LayoutInflater.from(activity).inflate(R.layout.new_deck_popup, null)
        val et = view.findViewById<EditText>(R.id.deck_name_et)
        val buttonPopup = view.findViewById<Button>(R.id.button_popup)

        // new popupwindow instance
        val popupWindow = PopupWindow(
            view, // Custom view to show in popup window
            LinearLayout.LayoutParams.WRAP_CONTENT, // Width of popup window
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true // Window height
        )
        //onclick listener
        buttonPopup.setOnClickListener {
            val word = et.text.toString()
            val time = System.currentTimeMillis()
            val deck = Deck(word, time)
            viewModel.insert(deck)
            viewModel.checkExistingName(deck)
            popupWindow.dismiss()
        }

        // dismiss listener
        popupWindow.setOnDismissListener {
//            listAdapter.notifyDataSetChanged()
        }
        //show popop window
         popupWindow.showAtLocation(view, Gravity.CENTER,0,0)
    }
    fun toDeckFragment() {
        val transaction = activity!!.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.top_frame, DeckFragment())
        transaction.addToBackStack(null)
        transaction.commit()
    }


}
