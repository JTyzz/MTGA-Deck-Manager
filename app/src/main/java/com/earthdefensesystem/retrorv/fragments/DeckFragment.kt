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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.earthdefensesystem.retrorv.R
import com.earthdefensesystem.retrorv.adapter.DeckAdapter
import com.earthdefensesystem.retrorv.model.CardCount
import com.github.mikephil.charting.charts.BarChart

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
        val recyclerView = view.findViewById<RecyclerView>(R.id.deck_rv)
        val decknameTV = view.findViewById<TextView>(R.id.deck_name_tv)
        val deckIV = view.findViewById<ImageView>(R.id.deck_background)
        val deckChart = view.findViewById<BarChart>(R.id.mana_chart)

        val deckAdapter =
            DeckAdapter(requireContext()) { cardCount: CardCount -> onItemClick(cardCount) }
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext())
        recyclerView.adapter = deckAdapter

//        viewModel.openDeckCard?.observe(viewLifecycleOwner, Observer { deckcard ->
//            deckcard?.let {
//                Log.d("deckcheck", "${it.deck.name} is loaded")
//                deckAdapter.loadCards(it.cards)
//                decknameTV.text = it.deck.name

//            }
//        })


        viewModel.mCurrentDeck?.observe(viewLifecycleOwner, Observer { cards ->
            cards.let {
                deckAdapter.loadCards(it.cards)
                viewModel.drawChart(deckChart, it.cards)
            }
        })


        val callback = requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner) {
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.remove(DeckFragment())
                transaction.remove(SearchFragment())
                requireActivity().findViewById<FrameLayout>(R.id.top_frame).visibility = View.GONE
                requireActivity().findViewById<FrameLayout>(R.id.bottom_frame).visibility =
                    View.GONE
                requireActivity().findViewById<FrameLayout>(R.id.screen_frame).visibility =
                    View.VISIBLE
                transaction.replace(
                    R.id.screen_frame,
                    ListFragment()
                )
                transaction.commit()
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
            val deck = viewModel.mCurrentDeck?.value?.deck
            Log.d("salami", "${deck?.name} art button clicked")
            val deckIV = requireActivity().findViewById<ImageView>(R.id.deck_background)
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
}
