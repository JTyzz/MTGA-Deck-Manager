package com.earthdefensesystem.retrorv.network

import android.content.Context
import com.earthdefensesystem.retrorv.model.Card

class SearchRepo(private val api: APIService) : BaseRepo() {

    suspend fun getSearchCards(color: String): MutableList<Card>? {
        val cardResponse = safeApiCall(
            call = { api.getCardColor(color).await() },
            errorMessage = "Error fetching cards"
        )
        return cardResponse?.cards?.toMutableList()
    }

}