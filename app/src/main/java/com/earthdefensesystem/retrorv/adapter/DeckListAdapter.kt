package com.earthdefensesystem.retrorv.adapter

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.earthdefensesystem.retrorv.R
import com.earthdefensesystem.retrorv.model.Card
import com.earthdefensesystem.retrorv.model.Deck
import kotlinx.android.synthetic.main.deck_grid_item.view.*
class DeckListAdapter (var deckList:List<Deck>, var context: Context) :BaseAdapter() {

    override fun getItem(position: Int): Any {
        return deckList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return deckList.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var holder: ViewHolder
        var newView = convertView
        if (newView == null) {
            val mInflater = (context as Activity).layoutInflater

            newView = mInflater.inflate(R.layout.deck_grid_item, parent, false)

            holder = ViewHolder()

            holder.mTextView = newView!!.findViewById(R.id.deck_tv)

            newView.tag = holder
        } else {
            holder = newView.tag as ViewHolder
        }

        holder.mTextView!!.text = deckList[position].name

        return newView
    }

    class ViewHolder {
        var mTextView: TextView? = null
    }
}