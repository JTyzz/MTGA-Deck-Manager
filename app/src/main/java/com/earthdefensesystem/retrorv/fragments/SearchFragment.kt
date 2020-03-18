package com.earthdefensesystem.retrorv.fragments

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
import kotlinx.coroutines.runBlocking
import java.lang.StringBuilder

class SearchFragment : Fragment() {

    companion object {
        fun newInstance() =
            SearchFragment()
    }

    private lateinit var viewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        activity?.let {
            viewModel = ViewModelProvider(it).get((SharedViewModel::class.java))
        }
        val view: View = inflater.inflate(R.layout.search_fragment, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.search_rv)
        val fab = view.findViewById<FloatingActionButton>(R.id.search_fab)

        val searchAdapter =
            SearchAdapter(requireContext()) { cardItem: Card ->
                runBlocking {
                    cardItemClicked(cardItem)
                }}
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.adapter = searchAdapter

        viewModel.cardList.observe(viewLifecycleOwner, Observer { cards ->
            cards?.let { searchAdapter.loadCards(it)}
        })

        fab.setOnClickListener {
            showSearchDialog()
        }

        return view
    }


    private fun cardItemClicked(cardItem: Card) {
        runBlocking {
            viewModel.insertCardtoDeck(cardItem, 4)
        }
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
            val colorSearch = "c:$sb+f:standard"
            Toast.makeText(requireContext(), colorSearch, Toast.LENGTH_SHORT).show()
            viewModel.loadSearchCards(colorSearch)
        }
        builder.setNeutralButton("Cancel") { dialogInterface, _ ->
            dialogInterface.cancel()
        }
        val dialog = builder.create()
        dialog.show()

    }
}
