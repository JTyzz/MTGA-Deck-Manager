package com.earthdefensesystem.retrorv.adapter

import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.earthdefensesystem.retrorv.R
import com.earthdefensesystem.retrorv.SearchActivity
import com.earthdefensesystem.retrorv.model.Card
import com.earthdefensesystem.retrorv.model.Cards
import kotlinx.android.synthetic.main.card_front_item.view.*
import kotlinx.android.synthetic.main.card_list_item.view.*

class SearchAdapter internal constructor(context: Context, val clickListener: (Card) -> Unit) :
    RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    private var cards = emptyList<Card>()
    var onItemClick: ((position: Int, view: View) -> Unit)? = null

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
            val circularProgressDrawable = CircularProgressDrawable(itemView.context).also {
                it.strokeWidth = 5f
                it.centerRadius = 30f
                it.start()
            }
            itemView.setOnClickListener { clickListener(card) }
            Glide.with(itemView.context)
                .load(card.imageUris?.small)
 //               .apply(RequestOptions().placeholder(circularProgressDrawable))
                .into(itemView.card_iv)
        }

    }

    fun spannableImage(inputText: String) {

    }
}

