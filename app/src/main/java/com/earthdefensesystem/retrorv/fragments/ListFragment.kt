package com.earthdefensesystem.retrorv.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.earthdefensesystem.retrorv.R
import com.earthdefensesystem.retrorv.adapter.ListAdapter
import com.earthdefensesystem.retrorv.model.Deck
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

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
        val listAdapter = ListAdapter(requireContext()) { deckItem: Deck -> listItemClicked(deckItem) }
        recyclerView.adapter = listAdapter
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3) as RecyclerView.LayoutManager?

        viewModel.allLDDecks.observe(viewLifecycleOwner, Observer { decks ->
            decks?.let { listAdapter.loadDecks(it)}
        })
        viewModel.deckNamesLD.observe(viewLifecycleOwner, Observer {
            viewModel.deckNames = it.toMutableList()
        })


        svEt.setTextColor(resources.getColor(R.color.rose, activity?.theme))
        svEt.setHintTextColor(resources.getColor(R.color.rose, activity?.theme))

        ndBtn.setOnClickListener {
            runBlocking {
                viewModel.newDeck()
            }
            viewModel.mDeckId.observe(viewLifecycleOwner, Observer {
                toDeckFragment()
            })
        }
        return view
    }

    override fun onAttach(context: Context) {
        activity?.let {
            viewModel = ViewModelProvider(it).get(SharedViewModel::class.java)
        }
        super.onAttach(context)
        viewModel.refreshListUI()
    }
    private fun listItemClicked(deckItem: Deck){
        runBlocking {
            viewModel.setDeckId(deckItem.deckId!!)
        }
        toDeckFragment()
    }

    private fun toDeckFragment() {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.remove(ListFragment())
        requireActivity().findViewById<FrameLayout>(R.id.top_frame).visibility = View.VISIBLE
        requireActivity().findViewById<FrameLayout>(R.id.bottom_frame).visibility = View.VISIBLE
        requireActivity().findViewById<FrameLayout>(R.id.screen_frame).visibility = View.GONE
        transaction.replace(R.id.top_frame, DeckFragment())
        transaction.replace(R.id.bottom_frame, SearchFragment())
        transaction.addToBackStack(null)
        transaction.commit()
    }

}
