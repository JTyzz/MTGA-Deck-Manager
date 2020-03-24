package com.earthdefensesystem.retrorv.network

import retrofit2.Call
import com.earthdefensesystem.retrorv.model.Base
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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

    companion object {
        private const val BASE_URL = "https://api.scryfall.com"

        private val loggingInterceptor =
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)


        private val okHttp = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(UnencodeInterceptor())
            .build()

        fun createService(): APIService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .client(okHttp)
                .build()
                .create(APIService::class.java)
        }
    }
}

class UnencodeInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val request = chain.request()
        val path = request.url.toString()
        val string = path.replace("%2B", "+")
        val newRequest = request.newBuilder()
            .url(string).build()
        return chain.proceed(newRequest)
    }
}