package com.earthdefensesystem.retrorv.rest

import android.util.Log
import com.earthdefensesystem.retrorv.model.Card
import com.earthdefensesystem.retrorv.model.Cards

class SearchRepo(private val api: APIService) : BaseRepo() {

    suspend fun getSearchCards(color: String): MutableList<Card>? {
        val cardResponse = safeApiCall(
            call = { api.getCardColor(color).await() },
            errorMessage = "Error fetching cards"
        )
        return cardResponse?.cards?.toMutableList()
    }

}