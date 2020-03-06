package com.earthdefensesystem.retrorv.deck_activity

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.earthdefensesystem.retrorv.R
import com.earthdefensesystem.retrorv.model.Deck
import org.w3c.dom.Text

class DeckFragment : Fragment() {


    private lateinit var viewModel: SearchViewModel
    internal lateinit var deckid: TextView
    internal lateinit var deckname: TextView



    companion object {
        fun newInstance() = DeckFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.deck_fragment, container, false)
        activity?.let {
            viewModel = ViewModelProvider(it).get((SearchViewModel::class.java))
        }
        deckid = view.findViewById<TextView>(R.id.deck_id_tv)
        deckname = view.findViewById<TextView>(R.id.deck_name_tv)
        viewModel.openDeck.observe(this,
            Observer<Deck> { t ->
                deckid.text = t.deckId.toString()
                deckname.text = t.name
            })

        deckid.setOnClickListener{
            Toast.makeText(context, viewModel.openDeck.value?.name, Toast.LENGTH_LONG).show()
        }
        return view
    }


}
