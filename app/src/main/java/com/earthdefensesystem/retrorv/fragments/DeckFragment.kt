package com.earthdefensesystem.retrorv.fragments

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.earthdefensesystem.retrorv.R
import com.earthdefensesystem.retrorv.adapter.DeckAdapter
import com.earthdefensesystem.retrorv.adapter.SearchAdapter
import com.earthdefensesystem.retrorv.model.Card
import com.earthdefensesystem.retrorv.model.CardCount
import com.earthdefensesystem.retrorv.network.CardSearchDataSourceFactory
import com.github.mikephil.charting.charts.BarChart
import kotlinx.android.synthetic.main.deck_fragment.*
import kotlinx.coroutines.runBlocking
import java.lang.StringBuilder

class DeckFragment : Fragment() {

    companion object {
        fun newInstance() =
            DeckFragment()
    }

    private lateinit var viewModel: SharedViewModel
    private val searchAdapter = SearchAdapter{ card: Card -> cardItemClicked(card) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.let {
            viewModel = ViewModelProvider(it).get((SharedViewModel::class.java))
        }
        val view: View = inflater.inflate(R.layout.deck_fragment, container, false)
        val deckAdapter =
            DeckAdapter{ cardCount: CardCount -> deckItemClicked(cardCount) }
        val deckRV = view.findViewById<RecyclerView>(R.id.deck_rv)
        val editBtn =view.findViewById<Button>(R.id.edit_deck_btn)
        deckRV.layoutManager = LinearLayoutManager(requireContext())
        deckRV.adapter = deckAdapter

        val deckChart = view.findViewById<BarChart>(R.id.mana_chart)

        viewModel.mCurrentDeck?.observe(viewLifecycleOwner, Observer { cards ->
            cards.let {
                val cardList = it.cards.sortedWith(compareBy {
                        cardCount -> cardCount.card.cmc
                })
                deckAdapter.loadCards(cardList)
                viewModel.drawChart(deckChart, it.cards)
            }
        })

        editBtn.setOnClickListener {
            showSearchDialog()
        }
        return view
    }

    private fun deckItemClicked(deck: CardCount) {
        Log.d("salami", "hello ${deck.card.name}")
        makeAlertDialog(deck)
    }

    private fun cardItemClicked(cardItem: Card) {
        runBlocking {
            viewModel.insertCardtoDeck(cardItem, 4)
        }
    }

    private fun initSearch(query: String) {
        search_rv.layoutManager = LinearLayoutManager(requireContext())
        search_rv.adapter = searchAdapter
        val config = PagedList.Config.Builder()
            .setPageSize(175)
            .setEnablePlaceholders(false)
            .build()
        viewModel.mLiveData = initPagedListBuilder(config, query).build()

        viewModel.mLiveData.observe(viewLifecycleOwner, Observer<PagedList<Card>> { pagedList ->
            searchAdapter.submitList(pagedList)
        })
    }

    private fun initPagedListBuilder(config: PagedList.Config, query: String): LivePagedListBuilder<String, Card> {
        return LivePagedListBuilder(CardSearchDataSourceFactory(query), config)
    }

    private fun makeAlertDialog(cardItem: CardCount) {
        // inflate popup view
        val view = LayoutInflater.from(activity).inflate(R.layout.deck_card_popup, null)
        val et = view.findViewById<EditText>(R.id.card_count_et)
        val closeButton = view.findViewById<Button>(R.id.dc_popup_close_btn)
        val artButton = view.findViewById<Button>(R.id.dc_popup_art_btn)

        // new popupwindow instance
        val popupWindow = PopupWindow(
            view, // Custom view to show in popup window
            LinearLayout.LayoutParams.WRAP_CONTENT, // Width of popup window
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true // Window height
        )
        //onclick listener
        closeButton.setOnClickListener {
            val editText = et.text.toString()
            val number = editText.toInt()
            if (editText.isNotEmpty()) {
                viewModel.updateCardCount(cardItem, number)
            }
            popupWindow.dismiss()
        }

        artButton.setOnClickListener {
            val deck = viewModel.mCurrentDeck?.value?.deck
            Log.d("salami", "${deck?.name} art button clicked")
            val deckIV = requireActivity().findViewById<ImageView>(R.id.deck_background_iv)
            viewModel.updateDeckBackground(deck!!, cardItem, deckIV)
            popupWindow.dismiss()
        }

        // dismiss listener
        popupWindow.setOnDismissListener {
            //            listAdapter.notifyDataSetChanged()
        }
        //show popop window
        popupWindow.showAtLocation(view, Gravity.TOP, 0, 0)

    }

    private fun showSearchDialog() {
        val arrayColors = R.array.magic_colors
        val arrayChecked = booleanArrayOf(false, false, false, false, false)
        val builder = AlertDialog.Builder(requireContext())
        val colorsList = arrayOf("W", "B", "R", "U", "G")
        builder.setTitle("Select colors")
        builder.setMultiChoiceItems(arrayColors, arrayChecked) { _, i, isChecked ->
            arrayChecked[i] = isChecked
            val color = colorsList[i]
            Toast.makeText(requireContext(), "$color $isChecked", Toast.LENGTH_SHORT).show()
        }
        builder.setPositiveButton("Search") { _, _ ->
            val sb = StringBuilder()
            for (j in arrayChecked.indices) {
                val checked = arrayChecked[j]
                if (checked) {
                    sb.append(colorsList[j])
                }
            }
            val colorSearch = "c:$sb+f:standard"
            Toast.makeText(requireContext(), colorSearch, Toast.LENGTH_SHORT).show()
            initSearch(colorSearch)
//            viewModel.loadSearchCards(colorSearch)
        }
        builder.setNeutralButton("Cancel") { dialogInterface, _ ->
            dialogInterface.cancel()
        }
        val dialog = builder.create()
        dialog.show()

    }
}
