package com.earthdefensesystem.retrorv.adapter

import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.earthdefensesystem.retrorv.R
import com.earthdefensesystem.retrorv.model.Cards
import kotlinx.android.synthetic.main.card_list_item.view.*

class SearchAdapter internal constructor(context: Context, val clickListener: (Cards) -> Unit) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    private var cards = emptyList<Cards>()
    private var inflater: LayoutInflater = LayoutInflater.from(context)
    var onItemClick: ((position: Int, view: View) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_list_item, parent, false)
       return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(cards[position], clickListener)

    }

    override fun getItemCount(): Int {
        return cards.size
    }

    internal fun loadCards(cards: List<Cards>){
        this.cards = cards
        notifyDataSetChanged()
    }

    class ViewHolder(containerView: View) : RecyclerView.ViewHolder(containerView){
        fun bind(card: Cards, clickListener: (Cards) -> Unit){
            itemView.card_name.text = card.name
            itemView.card_text.text = card.text
            itemView.card_mana.text = card.manaCost
            itemView.setOnClickListener { clickListener(card) }
        }

    }

    fun spannableImage(inputText: String) {

    }
}