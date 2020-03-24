package com.earthdefensesystem.retrorv.network

import android.util.Log
import androidx.paging.PageKeyedDataSource
import com.earthdefensesystem.retrorv.model.Base
import com.earthdefensesystem.retrorv.model.Card
import kotlinx.coroutines.runBlocking

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
            Log.d("debug", "initial load")
            callback.onResult(item?.cards!!, null, item.nextPage)
        }
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, Card>) {
        runBlocking {
            val item = searchRepo.getNextPage(params.key)
            Log.d("debug", "after load")
            callback.onResult(item?.cards!!, item.nextPage)
        }
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, Card>) {
        //ignored, only need initial load
    }

}