package com.earthdefensesystem.retrorv

import androidx.appcompat.app.AppCompatActivity

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.earthdefensesystem.retrorv.adapter.SearchAdapter
import com.earthdefensesystem.retrorv.model.Cards
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_search.*
import java.lang.StringBuilder

class SearchActivity : AppCompatActivity() {
    private lateinit var searchViewModel: SearchViewModel
    private lateinit var container: ConstraintLayout
    private lateinit var searchAdapter: SearchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val searchView = findViewById<RecyclerView>(R.id.search_rv)
        val searchFab = findViewById<FloatingActionButton>(R.id.searchFab)

        searchAdapter = SearchAdapter(this)
            {cardItem: Cards -> cardItemClicked(cardItem)}
        searchView.adapter = searchAdapter
        searchView.layoutManager = GridLayoutManager(this, 2)

        searchViewModel = ViewModelProvider(this).get(SearchViewModel::class.java)

        searchViewModel.searchCardsLiveData.observe(this, Observer { cards ->
            cards?.let { searchAdapter.loadCards(it)}
        })



        container = findViewById(R.id.constraint_layout)

        searchFab.setOnClickListener {
            showSearchDialog()
        }

        expandable.setOnExpandListener {
            val set = ConstraintSet()
            set.clone(container)
            if (it) {
                toast("expanded")
                set.connect(expandable.id, ConstraintSet.TOP, black_btn.id, ConstraintSet.BOTTOM,10)
                set.applyTo(container)
            } else {
                toast("collapse")
                set.connect(expandable.id, ConstraintSet.TOP, black_btn.id, ConstraintSet.BOTTOM,10)
                set.applyTo(container)
            }
        }

        expandable.parentLayout.setOnClickListener {
            if (expandable.isExpanded) {
                expandable.collapse()
            } else {
                expandable.expand()
            }
        }
    }

    private fun showSearchDialog() {
        val arrayColors = R.array.magic_colors
        val arrayChecked = booleanArrayOf(false, false, false, false, false)
        val builder = AlertDialog.Builder(this@SearchActivity)
        val colorsList = arrayOf("White", "Black", "Red", "Blue", "Green")
        builder.setTitle("Select colors")
        builder.setMultiChoiceItems(arrayColors, arrayChecked) { _, i, isChecked ->
            arrayChecked[i] = isChecked
            val color = colorsList[i]
            Toast.makeText(applicationContext, "$color $isChecked", Toast.LENGTH_SHORT).show()
        }
        builder.setPositiveButton("Search") { _, _ ->
            val sb = StringBuilder()
            for (j in arrayChecked.indices) {
                val checked = arrayChecked[j]
                if (checked) {
                    sb.append(colorsList[j]).append("|")
                }
            }
            val colorSearch = sb.toString().substring(0, sb.length - 1)
            Toast.makeText(applicationContext, colorSearch, Toast.LENGTH_SHORT).show()
            searchViewModel.getCardsSearch(colorSearch)

        }
        builder.setNeutralButton("Cancel") { dialogInterface, _ ->
            dialogInterface.cancel()
        }
        val dialog = builder.create()
        dialog.show()

    }

    private fun toast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    private fun cardItemClicked(cardItem: Cards){
        Toast.makeText(this, "Clicked: ${cardItem.name}", Toast.LENGTH_LONG).show()
    }


}
