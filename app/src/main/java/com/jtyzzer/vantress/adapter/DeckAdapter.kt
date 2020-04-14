package com.jtyzzer.vantress.adapter

import android.view.*
import androidx.recyclerview.widget.RecyclerView
import com.jtyzzer.vantress.R
import com.jtyzzer.vantress.model.CardCount
import com.jtyzzer.vantress.utilities.CardBackgroundConverter
import com.jtyzzer.vantress.utilities.ImageSpanConverter
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
            itemView.card_mana.text =
                ImageSpanConverter.getSpannedImage(itemView.context, item.card.manaCost.toString())
            itemView.card_count.text = "${item.count}x"
            CardBackgroundConverter.setCardBGColor(itemView.card_list_layout_bg, item.card.colors!!)
        }
    }
}