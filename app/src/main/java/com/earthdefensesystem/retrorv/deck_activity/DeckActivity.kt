package com.earthdefensesystem.retrorv.deck_activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
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
                    R.id.screen_frame,
                    ListFragment.newInstance()
                )
                .commitNow()
        }
        //needed to bind viewmodel to activity to persist between fragments
        val SearchViewModel = ViewModelProvider(this).get(SearchViewModel::class.java)
    }

    //overrides back button press to reset visibilities and fragments
    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount
        if (count == 1){
            super.onBackPressed()
            val transaction = supportFragmentManager.beginTransaction()
            transaction.remove(DeckFragment())
            transaction.remove(SearchFragment())
            findViewById<FrameLayout>(R.id.top_frame).visibility = View.GONE
            findViewById<FrameLayout>(R.id.bottom_frame).visibility = View.GONE
            findViewById<FrameLayout>(R.id.screen_frame).visibility = View.VISIBLE
            transaction.replace(R.id.screen_frame, ListFragment())
            transaction.commit()
        } else {
            supportFragmentManager.popBackStack()
        }
    }
}
