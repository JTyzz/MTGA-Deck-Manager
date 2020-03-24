package com.earthdefensesystem.retrorv.network

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.earthdefensesystem.retrorv.model.Card

class CardSearchDataSourceFactory (
    private val searchQuery: String
) : DataSource.Factory<String, Card>() {

    val source = MutableLiveData<CardSearchDataSource>()

    override fun create(): DataSource<String, Card> {
        val source = CardSearchDataSource(searchQuery)
        this.source.postValue(source)
        return source
    }

}