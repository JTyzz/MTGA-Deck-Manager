package com.earthdefensesystem.retrorv.deck_activity

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.earthdefensesystem.retrorv.R
import com.earthdefensesystem.retrorv.adapter.SearchAdapter
import com.earthdefensesystem.retrorv.model.Card
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.deck_fragment.*
import java.lang.StringBuilder

class DeckFragment : Fragment() {

    companion object {
        fun newInstance() =
            DeckFragment()
    }

    private lateinit var viewModel: DeckViewModel
    private lateinit var searchAdapter: SearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get((DeckViewModel::class.java))

        val view: View = inflater.inflate(R.layout.deck_fragment, container, false)
        val activity = activity as Context
        val recyclerView = view.findViewById<RecyclerView>(R.id.search_rv)
        val fab = view.findViewById<FloatingActionButton>(R.id.search_fab)

        searchAdapter = SearchAdapter(activity)
        {cardItem: Card -> cardItemClicked(cardItem)}
        recyclerView.layoutManager = GridLayoutManager(activity, 2)
        recyclerView.adapter = searchAdapter
        viewModel.searchCardsLiveData.observe(viewLifecycleOwner, Observer { cards ->
            cards?.let { searchAdapter.loadCards(it)}
        })

        fab.setOnClickListener {
                showSearchDialog()
            }

        return view
    }


    private fun cardItemClicked(cardItem: Card){
        Toast.makeText(requireContext(), "Clicked: ${cardItem.name}", Toast.LENGTH_LONG).show()
    }

    private fun showSearchDialog() {
        val arrayColors = R.array.magic_colors
        val arrayChecked = booleanArrayOf(false, false, false, false, false)
        val builder = AlertDialog.Builder(requireContext())
        val colorsList = arrayOf("W", "B", "R", "U", "G")
        builder.setTitle("Select colors")
        builder.setMultiChoiceItems(arrayColors, arrayChecked) { _, i, isChecked ->
            arrayChecked[i] = isChecked
            val color = colorsList[i]
            Toast.makeText(requireContext(), "$color $isChecked", Toast.LENGTH_SHORT).show()
        }
        builder.setPositiveButton("Search") { _, _ ->
            val sb = StringBuilder()
            for (j in arrayChecked.indices) {
                val checked = arrayChecked[j]
                if (checked) {
                    sb.append(colorsList[j])
                }
            }
            val colorSearch = "c:$sb"
            Toast.makeText(requireContext(), colorSearch, Toast.LENGTH_SHORT).show()
            viewModel.getCardsSearch(colorSearch)
        }
        builder.setNeutralButton("Cancel") { dialogInterface, _ ->
            dialogInterface.cancel()
        }
        val dialog = builder.create()
        dialog.show()

    }
}
