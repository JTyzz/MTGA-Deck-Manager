package com.earthdefensesystem.retrorv

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.earthdefensesystem.retrorv.fragments.ListFragment
import com.earthdefensesystem.retrorv.fragments.SharedViewModel

class DeckActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.deck_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(
                    R.id.nav_host_fragment_container,
                    ListFragment.newInstance()
                )
                .commitNow()
        }
        //bind viewmodel to activity to persist between fragments
        val viewModel = ViewModelProvider(this).get(SharedViewModel::class.java)

        viewModel.mDeckId.observe(this, Observer {
            Log.d("debug", "New deck id $it")
        })
    }

}
