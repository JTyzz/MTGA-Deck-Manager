package com.earthdefensesystem.retrorv.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.earthdefensesystem.retrorv.R
import com.earthdefensesystem.retrorv.model.Card
import kotlinx.android.synthetic.main.card_front_item.view.*
import kotlinx.android.synthetic.main.card_list_item.view.*

class DeckAdapter (context: Context, val clickListener: (Card) -> Unit) : RecyclerView.Adapter<DeckAdapter.ViewHolder>() {

    var onItemClick: ((position: Int, view: View) -> Unit)? = null
    private var cards = emptyList<Card>()

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

    internal fun loadCards(cards: List<Card>) {
        this.cards = cards
        notifyDataSetChanged()
    }

    class ViewHolder(containerView: View) : RecyclerView.ViewHolder(containerView) {

        fun bind(card: Card, clickListener: (Card) -> Unit) {
            itemView.setOnClickListener { clickListener(card) }
            itemView.card_name.text = card.name
            itemView.card_mana.text = card.cmc.toString()
        }

    }
}