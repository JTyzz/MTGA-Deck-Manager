package com.earthdefensesystem.retrorv.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.earthdefensesystem.retrorv.R
import com.earthdefensesystem.retrorv.model.Deck
import com.earthdefensesystem.retrorv.utilities.ImageStoreManager
import kotlinx.android.synthetic.main.deck_grid_item.view.*


class ListAdapter internal constructor(context: Context, val clickListener: (Deck) -> Unit) : RecyclerView.Adapter<ListAdapter.ViewHolder>() {

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

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(deck: Deck, clickListener: (Deck) -> Unit){
            itemView.deck_tv.text = deck.name
            itemView.setOnClickListener { clickListener(deck) }
            if (deck.uri != null){
                val bitmap = ImageStoreManager
                .getImageFromInternalStorage(itemView.context, deck.uri!!)
            Glide.with(itemView.context)
                .load(bitmap)
                .into(itemView.deck_iv)
            }
        }

    }
}