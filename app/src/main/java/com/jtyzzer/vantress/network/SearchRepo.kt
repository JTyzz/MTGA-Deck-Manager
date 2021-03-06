package com.jtyzzer.vantress.network

import com.jtyzzer.vantress.model.Base

class SearchRepo() : BaseRepo() {
    private val api = APIService.createService()

    suspend fun getSearchCards(color: String): Base? {
        return safeApiCall(
            call = { api.getCardColor(color).await() },
            errorMessage = "Error fetching cards"
        )
    }

    suspend fun getNextPage(url: String): Base? {
        return safeApiCall(
            call = { api.getNextPage(url).await() },
            errorMessage = "Error getting next page"
        )
    }
}