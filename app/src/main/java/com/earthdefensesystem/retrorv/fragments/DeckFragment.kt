package com.earthdefensesystem.retrorv.fragments

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.earthdefensesystem.retrorv.R
import com.earthdefensesystem.retrorv.adapter.DeckAdapter
import com.earthdefensesystem.retrorv.adapter.SearchAdapter
import com.earthdefensesystem.retrorv.model.Card
import com.earthdefensesystem.retrorv.model.CardCount
import com.github.mikephil.charting.charts.BarChart
import kotlinx.coroutines.runBlocking
import java.lang.StringBuilder

class DeckFragment : Fragment() {


    companion object {
        fun newInstance() =
            DeckFragment()
    }


    private lateinit var viewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.let {
            viewModel = ViewModelProvider(it).get((SharedViewModel::class.java))
        }
        Log.d("debug", "New deck fragment created!")
        val view: View = inflater.inflate(R.layout.deck_fragment, container, false)
        val deckRV = view.findViewById<RecyclerView>(R.id.deck_rv)
        val deckAdapter =
            DeckAdapter(requireContext()) { cardCount: CardCount -> onItemClick(cardCount) }
        deckRV.layoutManager =
            LinearLayoutManager(requireContext())
        deckRV.adapter = deckAdapter

        val searchRV = view.findViewById<RecyclerView>(R.id.search_rv)
        val searchAdapter =
            SearchAdapter(requireContext()) { cardItem: Card ->
                runBlocking {
                    cardItemClicked(cardItem)
                }}
        searchRV.layoutManager = GridLayoutManager(requireContext(), 2)
        searchRV.adapter = searchAdapter
        val editDeckBtn = view.findViewById<Button>(R.id.edit_deck_btn)
        val deckChart = view.findViewById<BarChart>(R.id.mana_chart)



        viewModel.mCurrentDeck?.observe(viewLifecycleOwner, Observer { cards ->
            cards.let {
                deckAdapter.loadCards(it.cards)
                viewModel.drawChart(deckChart, it.cards)
            }
        })

        viewModel.cardList.observe(viewLifecycleOwner, Observer { cards ->
            cards?.let { searchAdapter.loadCards(it)}
        })


        val callback = requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner) {
                requireActivity().supportFragmentManager.popBackStackImmediate()
            }
        callback.isEnabled

        return view
    }



    private fun onItemClick(deck: CardCount) {
        Log.d("salami", "hello ${deck.card.name}")
        makeAlertDialog(deck)
    }

    private fun cardItemClicked(cardItem: Card) {
        runBlocking {
            viewModel.insertCardtoDeck(cardItem, 4)
        }
    }

    private fun makeAlertDialog(cardItem: CardCount) {
        // inflate popup view
        val view = LayoutInflater.from(activity).inflate(R.layout.deck_card_popup, null)
        val et = view.findViewById<EditText>(R.id.card_count_et)
        val closeButton = view.findViewById<Button>(R.id.dc_popup_close_btn)
        val artButton = view.findViewById<Button>(R.id.dc_popup_art_btn)

        // new popupwindow instance
        val popupWindow = PopupWindow(
            view, // Custom view to show in popup window
            LinearLayout.LayoutParams.WRAP_CONTENT, // Width of popup window
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true // Window height
        )
        //onclick listener
        closeButton.setOnClickListener {
            val editText = et.text.toString()
            val number = editText.toInt()
            if (editText.isNotEmpty()) {
                viewModel.updateCardCount(cardItem, number)
            }
            popupWindow.dismiss()
        }

        artButton.setOnClickListener {
            val deck = viewModel.mCurrentDeck?.value?.deck
            Log.d("salami", "${deck?.name} art button clicked")
            val deckIV = requireActivity().findViewById<ImageView>(R.id.deck_background_iv)
            viewModel.updateDeckBackground(deck!!, cardItem, deckIV)
            popupWindow.dismiss()
        }

        // dismiss listener
        popupWindow.setOnDismissListener {
            //            listAdapter.notifyDataSetChanged()
        }
        //show popop window
        popupWindow.showAtLocation(view, Gravity.TOP, 0, 0)

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
