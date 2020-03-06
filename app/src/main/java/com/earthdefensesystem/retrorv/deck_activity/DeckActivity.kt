package com.earthdefensesystem.retrorv.deck_activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.replace
import androidx.lifecycle.ViewModelProvider
import com.earthdefensesystem.retrorv.R

class DeckActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.deck_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(
                    R.id.top_frame,
                    ListFragment.newInstance()
                ).add(
                    R.id.bottom_frame,
                    SearchFragment.newInstance())
                .commitNow()
        }

        val SearchViewModel = ViewModelProvider(this).get(SearchViewModel::class.java)

    }
}
