package com.earthdefensesystem.retrorv.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.earthdefensesystem.retrorv.R
import com.earthdefensesystem.retrorv.model.Card
import kotlinx.android.synthetic.main.card_front_item.view.*

class SearchAdapter internal constructor(context: Context, val clickListener: (Card) -> Unit) :
    RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    private var cards = emptyList<Card>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.card_front_item, parent, false)
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
            Glide.with(itemView.context)
                .setDefaultRequestOptions(RequestOptions().also {
                    it.error(R.drawable.ic_x)
                    it.diskCacheStrategy(DiskCacheStrategy.ALL)
                    it.format(DecodeFormat.PREFER_RGB_565)
                })
                .load(card.imageUris?.normal)
                .thumbnail(0.5f)
                .into(itemView.card_iv)


        }

    }

    fun spannableImage(inputText: String) {

    }
}

