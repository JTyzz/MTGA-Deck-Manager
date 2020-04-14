package com.jtyzzer.vantress

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.jtyzzer.vantress.fragments.ListFragment
import com.jtyzzer.vantress.fragments.SharedViewModel

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

    }

}
