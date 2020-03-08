package com.earthdefensesystem.retrorv.deck_activity

import android.os.Bundle
import android.util.Log
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
import com.earthdefensesystem.retrorv.adapter.DeckAdapter
import com.earthdefensesystem.retrorv.model.CardCount
import com.earthdefensesystem.retrorv.model.Deck

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

        val deckAdapter = DeckAdapter(requireContext()) {
                cardCount: CardCount -> onItemClick(cardCount) }
        recyclerView.layoutManager =
            GridLayoutManager(requireContext(), 2) as RecyclerView.LayoutManager?
        recyclerView.adapter = deckAdapter

//        viewModel.allLDDecks.observe(viewLifecycleOwner, Observer { cardcount ->
//            cardcount?.let { deckAdapter.loadCards(it) }
//        })
        viewModel.openDeckCard.observe(viewLifecycleOwner, Observer {deckcard ->
            deckcard?.let { deckAdapter.loadCards(it.cards) }
        })
//            viewModel.getCardsByDeckId(viewModel.openDeckCard.value?.deckId!!)


        return view
    }

    private fun onItemClick(deck: CardCount) {
        Log.d("salami", "hello ${deck.card.name}")
        Toast.makeText(requireContext(), "Clicked: ${deck.card.name}", Toast.LENGTH_LONG).show()
    }

//    private fun makeAlertDialog(cardItem: CardCount){
//        // inflate popup view
//        val view = LayoutInflater.from(activity).inflate(R.layout.deck_card_popup, null)
//        val et = view.findViewById<EditText>(R.id.card_count_et)
//        val closeButton = view.findViewById<Button>(R.id.dc_popup_close_btn)
//        val artButton = view.findViewById<Button>(R.id.dc_popup_art_btn)
//
//        // new popupwindow instance
//        val popupWindow = PopupWindow(
//            view, // Custom view to show in popup window
//            LinearLayout.LayoutParams.WRAP_CONTENT, // Width of popup window
//            LinearLayout.LayoutParams.WRAP_CONTENT,
//            true // Window height
//        )
//        //onclick listener
//        closeButton.setOnClickListener {
//            val editText = et.text.toString()
//            val number = editText.toInt()
//            viewModel.updateCardCount(cardItem, number)
//            popupWindow.dismiss()
//        }
//
//        artButton.setOnClickListener {
//            val deck = viewModel.openDeck.value
//            viewModel.updateDeckBackground(deck!!, cardItem)
//            viewModel.openDeck
//            popupWindow.dismiss()
//        }
//
//        // dismiss listener
//        popupWindow.setOnDismissListener {
//            //            listAdapter.notifyDataSetChanged()
//        }
//        //show popop window
//        popupWindow.showAtLocation(view, Gravity.TOP,0,0)
//    }


}
