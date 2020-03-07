package com.earthdefensesystem.retrorv.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.earthdefensesystem.retrorv.R
import com.earthdefensesystem.retrorv.model.Card
import com.earthdefensesystem.retrorv.model.CardCount
import kotlinx.android.synthetic.main.card_front_item.view.*
import kotlinx.android.synthetic.main.card_list_item.view.*

class DeckAdapter (context: Context, val clickListener: (CardCount) -> Unit) : RecyclerView.Adapter<DeckAdapter.ViewHolder>() {

    var onItemClick: ((position: Int, view: View) -> Unit)? = null
    private var cards = emptyList<CardCount>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.card_list_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(cards[position], clickListener)

    }

    override fun getItemCount(): Int {
        return cards.size
    }

    internal fun loadCards(cards: List<CardCount>) {
        this.cards = cards
        notifyDataSetChanged()
    }

    class ViewHolder(containerView: View) : RecyclerView.ViewHolder(containerView) {

        fun bind(carditem: CardCount, clickListener: (CardCount) -> Unit) {
            itemView.setOnClickListener { clickListener(carditem) }
            itemView.card_name.text = carditem.card.name
            itemView.card_mana.text = carditem.count.toString()
        }

    }
}