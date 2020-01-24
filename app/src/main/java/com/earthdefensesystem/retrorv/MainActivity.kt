package com.earthdefensesystem.retrorv

import androidx.appcompat.app.AppCompatActivity

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.earthdefensesystem.retrorv.adapter.ListAdapter
import com.earthdefensesystem.retrorv.model.Base
import com.earthdefensesystem.retrorv.model.Cards
import com.earthdefensesystem.retrorv.rest.APIService
import com.earthdefensesystem.retrorv.rest.RestClient
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
class MainActivity : AppCompatActivity() {

    private var mApiService: APIService? = null
    //private val checkedItems: BooleanArray? = null
    private var mAdapter: ListAdapter?= null
    private var mCards: MutableList<Cards> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var colorArray = this.resources.getStringArray(R.array.magic_colors)


        val recyclerView: RecyclerView = findViewById(R.id.listRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        mAdapter = ListAdapter(this, mCards, R.layout.card_list_item)
        recyclerView.adapter = mAdapter

        mApiService = RestClient.client.create(APIService::class.java)

        getAllCards()



        white_btn.setOnClickListener {
            val builder = AlertDialog.Builder(this@MainActivity)
            builder.
        }


        green_btn.setOnClickListener {
            getCardColor("green")
        }
        black_btn.setOnClickListener {
            getCardColor("black")
        }
    }

    private fun getAllCards() {
        val call = mApiService!!.getAllCards()
        call.enqueue(object : Callback<Base> {

            override fun onResponse(call: Call<Base>, response: Response<Base>) {

                Log.d(TAG, "Total Cards: " + response.body()!!.cards.size)
                val resCards = response.body()
                if (resCards != null) {
                    mCards.addAll(resCards.cards)
                    mAdapter!!.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<Base>, t: Throwable) {
                Log.e(TAG, "Got error : " + t.localizedMessage)
            }
        })
    }


    private fun getCardColor(color: String){
        val call = mApiService!!.getCardColor(color)
        call.enqueue(object : Callback<Base> {

            override fun onResponse(call: Call<Base>, response: Response<Base>) {

                val questions = response.body()
                if (questions != null) {
                    mCards.clear()
                    mCards.addAll(questions.cards)
                    mAdapter!!.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<Base>, t: Throwable) {
                Log.e(TAG, "Got error : " + t.localizedMessage)
            }
        })
    }
    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

}
