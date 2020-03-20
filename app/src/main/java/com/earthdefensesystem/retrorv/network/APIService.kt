package com.earthdefensesystem.retrorv.network

import retrofit2.Call
import com.earthdefensesystem.retrorv.model.Base
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface APIService {
    @GET("/cards")
    fun getAllCards(): Call<Base>

    @GET("/cards/search")
    fun getCardColor(@Query("q") color: String): Deferred<Response<Base>>

    @GET
    fun getNextPage(@Url nextPage: String): Deferred<Response<Base>>
}