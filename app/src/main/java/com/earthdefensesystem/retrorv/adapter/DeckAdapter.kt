package com.earthdefensesystem.retrorv.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.earthdefensesystem.retrorv.R
import com.earthdefensesystem.retrorv.model.CardCount
import com.earthdefensesystem.retrorv.utilities.CardBackgroundConverter
import com.earthdefensesystem.retrorv.utilities.ImageSpanConverter
import kotlinx.android.synthetic.main.card_list_item.view.*

class DeckAdapter internal constructor(val clickListener: (CardCount) -> Unit) :
    RecyclerView.Adapter<DeckAdapter.ViewHolder>() {

    private var list = emptyList<CardCount>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.card_list_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position], clickListener)

    }

    override fun getItemCount(): Int {
        return list.size
    }

    internal fun loadCards(list: List<CardCount>) {
        this.list = list
        notifyDataSetChanged()
    }

    class ViewHolder(containerView: View) : RecyclerView.ViewHolder(containerView) {

        fun bind(item: CardCount, clickListener: (CardCount) -> Unit) {
            itemView.setOnClickListener { clickListener(item) }

            itemView.card_name.text = item.card.name
            itemView.card_mana.text = ImageSpanConverter.getSpannedImage(itemView.context ,item.card.manaCost.toString())
            itemView.card_count.text = "${item.count}x"
            CardBackgroundConverter.setCardBGColor(itemView.card_list_layout_bg, item.card.colors!!)
        }

    }
}