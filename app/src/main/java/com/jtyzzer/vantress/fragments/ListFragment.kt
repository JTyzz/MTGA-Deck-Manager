package com.jtyzzer.vantress.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jtyzzer.vantress.R
import com.jtyzzer.vantress.adapter.ListAdapter
import com.jtyzzer.vantress.model.DeckWithCards
import kotlinx.coroutines.*
import java.lang.Exception

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

        ndBtn.setOnClickListener {
            runBlocking {
                val res = async { viewModel.insertDeck("New Deck") }
                viewModel.setDeck(res.await())
                toDeckFragment(res.await())
            }
        }
        return view
    }

    private fun listItemClicked(deckItem: DeckWithCards) {
        runBlocking {
            val res = async { viewModel.setDeck(deckItem.deck.deckId!!) }
            res.await()
            toDeckFragment(deckItem.deck.deckId!!)
        }
    }

    private fun toDeckFragment(deckId: Long) {
        val action = ListFragmentDirections.actionListFragmentToDeckFragment(deckId)
        try {
            view?.findNavController()?.navigate(action)
        } catch (e: Exception) {
            Log.d("Exception", "listfragment: $e")
        }
    }
}
