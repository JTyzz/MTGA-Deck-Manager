package com.earthdefensesystem.retrorv.deck_activity

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.earthdefensesystem.retrorv.R
import com.earthdefensesystem.retrorv.adapter.DeckAdapter
import com.earthdefensesystem.retrorv.model.Card
import com.earthdefensesystem.retrorv.model.CardCount
import com.earthdefensesystem.retrorv.model.Deck
import org.w3c.dom.Text

class DeckFragment : Fragment() {


    private lateinit var viewModel: SearchViewModel
    private lateinit var deckAdapter: DeckAdapter
    internal lateinit var deckid: TextView
    internal lateinit var deckname: TextView



    companion object {
        fun newInstance() = DeckFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.let {
            viewModel = ViewModelProvider(it).get((SearchViewModel::class.java))
        }
        val view: View = inflater.inflate(R.layout.deck_fragment, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.deck_rv)

        deckAdapter = DeckAdapter(requireContext()) {
                cardItem: CardCount -> cardItemClicked(cardItem)}
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = deckAdapter

        deckid = view.findViewById(R.id.deck_id_tv)
        deckname = view.findViewById(R.id.deck_name_tv)
        viewModel.deckCardsLiveData.observe(viewLifecycleOwner, Observer {
            cardcount -> cardcount?.let {
            deckAdapter.loadCards(it)
        }
        })
        viewModel.openDeck.observe(this,
            Observer<Deck> { t ->
                deckname.text = t.name
            })

        viewModel.openDeck.observe(this, Observer {
            viewModel.getCardsByDeckId(viewModel.openDeck.value?.deckId!!)
        })
        deckid.setOnClickListener{
            Toast.makeText(context, viewModel.openDeck.value?.name, Toast.LENGTH_LONG).show()
        }
        return view
    }

    private fun cardItemClicked(cardItem: CardCount){
        Toast.makeText(requireContext(), "Clicked: ${cardItem.card.name}", Toast.LENGTH_LONG).show()
    }

    private fun makeAlertDialog(cardItem: CardCount){
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
            viewModel.changeCardCount(cardItem, number)
            popupWindow.dismiss()
        }

        // dismiss listener
        popupWindow.setOnDismissListener {
            //            listAdapter.notifyDataSetChanged()
        }
        //show popop window
        popupWindow.showAtLocation(view, Gravity.CENTER,0,0)
    }


}
