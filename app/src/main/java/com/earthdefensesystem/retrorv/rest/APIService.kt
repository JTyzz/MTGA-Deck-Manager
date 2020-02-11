package com.earthdefensesystem.retrorv.rest

import retrofit2.Call
import com.earthdefensesystem.retrorv.model.Base
import com.earthdefensesystem.retrorv.model.Cards
import retrofit2.http.GET
import retrofit2.http.Query

interface APIService {
    @GET("/v1/cards")
    fun getAllCards(): Call<Base>

    @GET("/v1/cards")
    fun getCardColor(@Query("colors") color:String): Call<Base>
}