package com.jtyzzer.vantress.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jtyzzer.vantress.R
import com.jtyzzer.vantress.model.DeckWithCards
import com.jtyzzer.vantress.utilities.ImageSpanConverter
import com.jtyzzer.vantress.utilities.ImageStoreManager
import kotlinx.android.synthetic.main.deck_grid_item.view.*
import java.lang.Exception


class ListAdapter internal constructor(
    context: Context,
    val clickListener: (DeckWithCards) -> Unit
) :
    RecyclerView.Adapter<ListAdapter.ViewHolder>() {

    var decks = emptyList<DeckWithCards>()
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    var onItemClick: ((position: Int, view: View) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = inflater.inflate(R.layout.deck_grid_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return decks.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(decks[position], clickListener)
    }

    internal fun loadDecks(decks: List<DeckWithCards>?) {
        this.decks = decks!!
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(deck: DeckWithCards, clickListener: (DeckWithCards) -> Unit) {
            itemView.deck_tv.text = deck.deck.name
            if (deck.deck.cIdentity != null) {
                itemView.deck_color_id.text =
                    ImageSpanConverter.getSpannedManaImage(itemView.context, deck.deck.cIdentity!!)
            }
            itemView.setOnClickListener { clickListener(deck) }
            if (deck.deck.uri != null) {
                try {
                    val bitmap = ImageStoreManager
                        .getImageFromInternalStorage(itemView.context, deck.deck.uri!!)
                    Glide.with(itemView.context)
                        .load(bitmap)
                        .into(itemView.deck_iv)
                } catch (e: Exception) {
                    Log.d("Exception", "listadapter $e")
                }
            }
        }
    }
}