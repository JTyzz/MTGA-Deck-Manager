package com.earthdefensesystem.retrorv.deck_activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.earthdefensesystem.retrorv.R

class DeckActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.deck_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.container,
                    DeckFragment.newInstance()
                )
                .commitNow()
        }
    }

}
