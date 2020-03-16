package com.earthdefensesystem.retrorv.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiFactory {
    //private const val BASE_URL = "https://api.magicthegathering.io"
    private const val BASE_URL = "https://api.scryfall.com"

    private val loggingInterceptor =
        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)


    private val okHttp = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(UnencodeInterceptor())
        .build()

    fun retrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .client(okHttp)
        .build()


    val apiService: APIService = retrofit().create(APIService::class.java)
}
class UnencodeInterceptor : Interceptor{
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.toString()
        val string = path.replace("%2B", "+")
        val newRequest = request.newBuilder()
            .url(string).build()
        return chain.proceed(newRequest)
    }
}