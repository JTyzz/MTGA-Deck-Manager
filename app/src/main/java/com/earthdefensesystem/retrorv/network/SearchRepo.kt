package com.earthdefensesystem.retrorv.network

import com.earthdefensesystem.retrorv.model.Base

class SearchRepo(private val api: APIService) : BaseRepo() {

    suspend fun getSearchCards(color: String): Base? {
        return safeApiCall(
        call = { api.getCardColor(color).await() },
        errorMessage = "Error fetching cards"
    )
    }

    suspend fun getNextPage(url: String): Base? {
        return safeApiCall(
            call = {api.getNextPage(url).await() },
            errorMessage = "Error getting next page"
        )
    }
}