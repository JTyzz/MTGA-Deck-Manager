package com.earthdefensesystem.retrorv.adapter

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.earthdefensesystem.retrorv.R
import com.earthdefensesystem.retrorv.model.Card
import com.earthdefensesystem.retrorv.model.CardCount
import com.earthdefensesystem.retrorv.model.Deck
import com.earthdefensesystem.retrorv.utilities.CardBackgroundConverter
import com.earthdefensesystem.retrorv.utilities.ImageSpanConverter
import kotlinx.android.synthetic.main.card_front_item.view.*
import kotlinx.android.synthetic.main.card_list_item.view.*

class DeckAdapter internal constructor(context: Context, val clickListener: (CardCount) -> Unit) :
    RecyclerView.Adapter<DeckAdapter.ViewHolder>() {

    private var cardcounts = emptyList<CardCount>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.card_list_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(cardcounts[position], clickListener)

    }

    override fun getItemCount(): Int {
        return cardcounts.size
    }

    internal fun loadCards(cardcounts: List<CardCount>) {
        this.cardcounts = cardcounts
        notifyDataSetChanged()
    }

    class ViewHolder(containerView: View) : RecyclerView.ViewHolder(containerView) {

        fun bind(carditem: CardCount, clickListener: (CardCount) -> Unit) {
            itemView.setOnClickListener { clickListener(carditem) }

            itemView.card_name.text = carditem.card.name
            itemView.card_mana.text = ImageSpanConverter.getSpannedImage(itemView.context ,carditem.card.manaCost.toString())
            itemView.card_count.text = "${carditem.count}x"
            CardBackgroundConverter.setCardBGColor(itemView.card_list_layout_bg, carditem.card.colors!!)
        }

    }
}