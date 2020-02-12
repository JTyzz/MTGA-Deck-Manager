package com.earthdefensesystem.retrorv.adapter

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.earthdefensesystem.retrorv.R
import com.earthdefensesystem.retrorv.model.Cards
import com.earthdefensesystem.retrorv.model.Deck
import kotlinx.android.synthetic.main.deck_grid_item.view.*

data class DeckListAdapter (var deckList:List<Deck>, var context: Activity) :BaseAdapter() {

    override fun getItem(position: Int): Any {
        return deckList.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return deckList.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = View.inflate(context, R.layout.deck_grid_item, null)

        val name_tv = view.findViewById<TextView>(R.id.deck_tv)
        name_tv.text = deckList.get(position).name

        return view
    }
}