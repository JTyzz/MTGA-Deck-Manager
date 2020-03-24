package com.earthdefensesystem.retrorv.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.earthdefensesystem.retrorv.R
import com.earthdefensesystem.retrorv.model.Card
import com.earthdefensesystem.retrorv.model.CardCount
import com.earthdefensesystem.retrorv.network.DiffUtilCallback
import kotlinx.android.synthetic.main.card_front_item.view.*

class SearchAdapter( val clickListener: (Card) -> Unit) :
    PagedListAdapter<Card, SearchAdapter.ViewHolder>(DiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.card_front_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.itemView.setOnClickListener { clickListener(item!!) }
        Glide.with(holder.itemView.context)
            .setDefaultRequestOptions(RequestOptions().also {
                it.error(R.drawable.ic_x)
                it.diskCacheStrategy(DiskCacheStrategy.ALL)
                it.format(DecodeFormat.PREFER_RGB_565)
            })
            .load(item?.imageUris?.normal)
            .thumbnail(0.5f)
            .into(holder.itemView.card_iv)

    }

    class ViewHolder(containerView: View) : RecyclerView.ViewHolder(containerView) {

    }
}

