package com.earthdefensesystem.retrorv.rest

import retrofit2.Call
import com.earthdefensesystem.retrorv.model.Base
import com.earthdefensesystem.retrorv.model.Cards
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface APIService {
    @GET("/cards")
    fun getAllCards(): Call<Base>

    @GET("/cards/search")
    fun getCardColor(@Query("q") color: String): Deferred<Response<Base>>
}