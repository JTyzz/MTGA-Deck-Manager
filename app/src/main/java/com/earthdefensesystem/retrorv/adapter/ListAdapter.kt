package com.earthdefensesystem.retrorv.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.ScrollingTabContainerView
import androidx.recyclerview.widget.RecyclerView
import com.earthdefensesystem.retrorv.model.Cards
import kotlinx.android.synthetic.main.card_list_item.view.*

class ListAdapter(private val context: Context, private val mCards: List<Cards>, private val mRowLayout: Int) : RecyclerView.Adapter<ListAdapter.CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
       val view = LayoutInflater.from(parent.context).inflate(mRowLayout, parent, false)
       return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.cardName.text = mCards[position].name
        holder.cardText.text = mCards[position].text
    }

    override fun getItemCount(): Int {
        return mCards.size
    }

    class CardViewHolder(containerView: View) : RecyclerView.ViewHolder(containerView){
        val cardName = containerView.card_name!!
        val cardText = containerView.card_text!!
    }
}