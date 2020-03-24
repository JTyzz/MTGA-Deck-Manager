package com.earthdefensesystem.retrorv.network

import androidx.recyclerview.widget.DiffUtil
import com.earthdefensesystem.retrorv.model.Card

class DiffUtilCallback :DiffUtil.ItemCallback<Card>() {
    override fun areItemsTheSame(oldItem: Card, newItem: Card): Boolean {
        return oldItem.cardId == newItem.cardId
    }

    override fun areContentsTheSame(oldItem: Card, newItem: Card): Boolean {
        return oldItem.name == newItem.name
    }
}