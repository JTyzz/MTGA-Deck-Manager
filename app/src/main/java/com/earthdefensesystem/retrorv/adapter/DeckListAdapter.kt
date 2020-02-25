package com.earthdefensesystem.retrorv.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.earthdefensesystem.retrorv.R
import com.earthdefensesystem.retrorv.model.Cards
import com.earthdefensesystem.retrorv.model.Deck
import kotlinx.android.synthetic.main.deck_grid_item.view.*


class DeckListAdapter internal constructor(context: Context, val clickListener: (Deck) -> Unit) : RecyclerView.Adapter<DeckListAdapter.ViewHolder>() {

    private var decks = emptyList<Deck>()
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    var onItemClick: ((position: Int, view: View) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder {
        val itemView = inflater.inflate(R.layout.deck_grid_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return decks.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(decks[position], clickListener)
    }
    internal fun loadDecks(decks: List<Deck>){
        this.decks = decks
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(deck: Deck, clickListener: (Deck) -> Unit){
            itemView.deck_tv.text = deck.name
            itemView.setOnClickListener { clickListener(deck) }
        }

    }
}