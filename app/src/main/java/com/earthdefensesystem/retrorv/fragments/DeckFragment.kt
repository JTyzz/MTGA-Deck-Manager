package com.earthdefensesystem.retrorv.fragments

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.earthdefensesystem.retrorv.R
import com.earthdefensesystem.retrorv.adapter.DeckAdapter
import com.earthdefensesystem.retrorv.model.CardCount
import com.earthdefensesystem.retrorv.utilities.CardBackgroundConverter
import com.github.mikephil.charting.charts.BarChart

class DeckFragment : Fragment() {


    companion object {
        fun newInstance() =
            DeckFragment()
    }


    private lateinit var viewModel: SearchViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.let {
            viewModel = ViewModelProvider(it).get((SearchViewModel::class.java))
        }
        val view: View = inflater.inflate(R.layout.deck_fragment, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.deck_rv)
        val decknameTV = view.findViewById<TextView>(R.id.deck_name_tv)
        val deckIV = view.findViewById<ImageView>(R.id.deck_background)
        val deckChart = view.findViewById<BarChart>(R.id.mana_chart)

        val deckAdapter =
            DeckAdapter(requireContext()) { cardCount: CardCount -> onItemClick(cardCount) }
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext())
        recyclerView.adapter = deckAdapter

        viewModel.openDeckCard?.observe(viewLifecycleOwner, Observer { deckcard ->
            deckcard?.let {
                deckAdapter.loadCards(it.cards)
                decknameTV.text = it.deck.name
                if (it.deck.uri != null) {
                    Glide.with(requireContext())
                        .load(it.deck.uri)
                        .into(deckIV)
                }
                if (it.cards.isNotEmpty()) {
                    viewModel.drawChart(deckChart)
                }
            }
        })

        val callback = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            viewModel.deleteDeckById()
        }
        callback.isEnabled

        return view
    }

    private fun onItemClick(deck: CardCount) {
        Log.d("salami", "hello ${deck.card.name}")
        makeAlertDialog(deck)
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
            val deck = viewModel.openDeckCard?.value?.deck
            Log.d("salami", "${deck?.name} art button clicked")
            viewModel.updateDeckBackground(deck!!, cardItem)
            popupWindow.dismiss()
        }

        // dismiss listener
        popupWindow.setOnDismissListener {
            //            listAdapter.notifyDataSetChanged()
        }
        //show popop window
        popupWindow.showAtLocation(view, Gravity.TOP, 0, 0)
    }
}
