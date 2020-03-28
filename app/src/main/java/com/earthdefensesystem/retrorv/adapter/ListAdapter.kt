package com.earthdefensesystem.retrorv.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.earthdefensesystem.retrorv.R
import com.earthdefensesystem.retrorv.model.Deck
import com.earthdefensesystem.retrorv.model.DeckWithCards
import com.earthdefensesystem.retrorv.utilities.ImageSpanConverter
import com.earthdefensesystem.retrorv.utilities.ImageStoreManager
import kotlinx.android.synthetic.main.deck_grid_item.view.*
import java.lang.Exception


class ListAdapter internal constructor(context: Context, val clickListener: (DeckWithCards) -> Unit) :
    RecyclerView.Adapter<ListAdapter.ViewHolder>(), Filterable {

    private var decks = emptyList<DeckWithCards>()
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

    internal fun loadDecks(decks: List<DeckWithCards>) {
        this.decks = decks
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(deck: DeckWithCards, clickListener: (DeckWithCards) -> Unit) {
            itemView.deck_tv.text = deck.deck.name
            if (deck.deck.cIdentity != null){
            itemView.deck_color_id.text = ImageSpanConverter.getSpannedManaImage(itemView.context, deck.deck.cIdentity!!)
                }
            itemView.setOnClickListener { clickListener(deck) }
            if (deck.deck.uri != null) {
                try {
                    val bitmap = ImageStoreManager
                        .getImageFromInternalStorage(itemView.context, deck.deck.uri!!)
                    Glide.with(itemView.context)
                        .load(bitmap)
                        .into(itemView.deck_iv)
                } catch (e: Exception){
                    Log.d("debug", "listadapter $e")
                }
            }
        }

    }

    override fun getFilter(): Filter {
        return CustomFilter(decks, this)
    }
}

class CustomFilter(val list: List<DeckWithCards>, val adapter: ListAdapter) : Filter() {
    val inputList: List<Deck> = emptyList()
    override fun performFiltering(constraint: CharSequence?): FilterResults {
        var results: FilterResults = FilterResults()
        val inputArray = ArrayList<Deck>(inputList)
        if (constraint != null && constraint.isNotEmpty()) {
            var conString = constraint.toString().toUpperCase()
            var filteredList = ArrayList<Deck>()
            for (i in 0..inputArray.size) {
                if (inputArray[i].name.toUpperCase().contains(conString)) {
                    filteredList.add(inputArray[i])
                }
            }
            results.count = filteredList.size
            results.values = filteredList.toList()
        } else {
            results.count = inputList.size
            results.values = inputList
        }

        return results
    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
        adapter.loadDecks(results?.values as List<DeckWithCards>)
        adapter.notifyDataSetChanged()
    }
}