package com.earthdefensesystem.retrorv.fragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.earthdefensesystem.retrorv.R
import com.earthdefensesystem.retrorv.adapter.ListAdapter
import com.earthdefensesystem.retrorv.model.Deck
import com.earthdefensesystem.retrorv.model.DeckWithCards
import kotlinx.coroutines.*
import java.lang.Exception
import kotlin.random.Random

class ListFragment : Fragment() {


    companion object {
        fun newInstance() = ListFragment()
    }
    private lateinit var viewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.let {
            viewModel = ViewModelProvider(it).get(SharedViewModel::class.java)
        }
        val view = inflater.inflate(R.layout.list_fragment, container, false)
        val ndBtn = view.findViewById<Button>(R.id.new_deck_button)
        val sv = view.findViewById<SearchView>(R.id.list_searchview)
        val svEt = sv.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        val recyclerView = view.findViewById<RecyclerView>(R.id.list_rv)
        val listAdapter =
            ListAdapter(requireContext()) { deckItem: DeckWithCards -> listItemClicked(deckItem) }
        recyclerView.adapter = listAdapter
        recyclerView.layoutManager =
            GridLayoutManager(requireContext(), 3) as RecyclerView.LayoutManager?

        viewModel.allLDDecks.observe(viewLifecycleOwner, Observer { decks ->
            decks?.let { listAdapter.loadDecks(it) }
        })

        viewModel.deckNamesLD.observe(viewLifecycleOwner, Observer {
            viewModel.deckNames = it.toMutableList()
        })

        svEt.setTextColor(resources.getColor(R.color.rose, activity?.theme))
        svEt.setHintTextColor(resources.getColor(R.color.rose, activity?.theme))

        ndBtn.setOnClickListener {
            runBlocking {
                val res = async {viewModel.insertDeck("New Deck")}
                viewModel.setDeck(res.await())
                toDeckFragment(res.await())
            }
        }
        return view
    }

    private fun listItemClicked(deckItem: DeckWithCards) {
        runBlocking {
            val res = async{viewModel.setDeck(deckItem.deck.deckId!!)}
            res.await()
            toDeckFragment(deckItem.deck.deckId!!)
        }
    }

    private fun toDeckFragment(deckId: Long) {
        val action = ListFragmentDirections.actionListFragmentToDeckFragment(deckId)
        try {
            view?.findNavController()?.navigate(action)
        } catch (e: Exception) {
            Log.d("debug", "Error: $e")
        }
    }
}
