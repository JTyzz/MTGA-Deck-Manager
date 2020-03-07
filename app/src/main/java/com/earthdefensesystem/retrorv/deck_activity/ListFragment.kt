package com.earthdefensesystem.retrorv.deck_activity

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.earthdefensesystem.retrorv.R
import com.earthdefensesystem.retrorv.adapter.ListAdapter
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
        activity?.let {
            viewModel = ViewModelProvider(it).get(SearchViewModel::class.java)
        }
        val view = inflater.inflate(R.layout.list_fragment, container, false)
        val fab = view.findViewById<FloatingActionButton>(R.id.deck_fab)
        val recyclerView = view.findViewById<RecyclerView>(R.id.list_rv)
        val listAdapter = ListAdapter(requireContext()) { deckItem: Deck -> listItemClicked(deckItem) }
        recyclerView.adapter = listAdapter
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2) as RecyclerView.LayoutManager?

        viewModel.allLDDecks.observe(this, Observer { decks ->
            decks?.let { listAdapter.loadDecks(it)
            listAdapter.notifyDataSetChanged()}
        })
        viewModel.deckNamesLD.observe(this, Observer {
            viewModel.deckNames = it.toMutableList()
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
        val et = view.findViewById<EditText>(R.id.nd_name_et)
        val buttonPopup = view.findViewById<Button>(R.id.nd_popup_close_btn)

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
            viewModel.checkExistingName(deck)
            viewModel.insertDeck(deck)
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
        transaction.remove(ListFragment())
        activity!!.findViewById<FrameLayout>(R.id.top_frame).visibility = View.VISIBLE
        activity!!.findViewById<FrameLayout>(R.id.bottom_frame).visibility = View.VISIBLE
        activity!!.findViewById<FrameLayout>(R.id.screen_frame).visibility = View.GONE
        transaction.replace(R.id.top_frame, DeckFragment())
        transaction.replace(R.id.bottom_frame, SearchFragment())
        transaction.addToBackStack(null)
        transaction.commit()

    }
}
