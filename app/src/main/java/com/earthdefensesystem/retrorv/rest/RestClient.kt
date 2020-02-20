package com.earthdefensesystem.retrorv.rest

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RestClient {
    private const val BASE_URL = "https://api.magicthegathering.io"
    private var mRetrofit: Retrofit? = null

    //increase timeout length mtg.io can be slow
    private val okHttp = OkHttpClient.Builder()
        .callTimeout(1, TimeUnit.MINUTES)
        .connectTimeout(1, TimeUnit.MINUTES)
        .readTimeout(1, TimeUnit.MINUTES)
        .writeTimeout(1, TimeUnit.MINUTES)


    val client: Retrofit
        get() {
            if (mRetrofit == null) {
                mRetrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttp.build())
                    .build()
            }
            return this.mRetrofit!!
        }
}