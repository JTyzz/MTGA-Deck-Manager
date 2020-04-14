package com.jtyzzer.vantress.network

import android.util.Log
import androidx.paging.PageKeyedDataSource
import com.jtyzzer.vantress.model.Card
import kotlinx.coroutines.runBlocking
import java.lang.Exception

class CardSearchDataSource(
    private val searchQuery: String
) : PageKeyedDataSource<String, Card>() {

    private val searchRepo = SearchRepo()
    override fun loadInitial(
        params: LoadInitialParams<String>,
        callback: LoadInitialCallback<String, Card>
    ) {
        runBlocking {
            val item = searchRepo.getSearchCards(searchQuery)
            try {
                callback.onResult(item?.cards!!, null, item.nextPage)
            } catch (e: Exception) {
                Log.d("Exception", "load initial exception $e")
            }
        }
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, Card>) {
        runBlocking {
            val item = searchRepo.getNextPage(params.key)
            callback.onResult(item?.cards!!, item.nextPage)
        }
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, Card>) {
        //ignored, only need initial load
    }

}