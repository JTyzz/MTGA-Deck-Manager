package com.earthdefensesystem.retrorv.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.addCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.*
import androidx.navigation.fragment.navArgs
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Fade
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.earthdefensesystem.retrorv.R
import com.earthdefensesystem.retrorv.adapter.DeckAdapter
import com.earthdefensesystem.retrorv.adapter.SearchAdapter
import com.earthdefensesystem.retrorv.model.Card
import com.earthdefensesystem.retrorv.model.CardCount
import com.earthdefensesystem.retrorv.network.CardSearchDataSourceFactory
import com.github.mikephil.charting.charts.BarChart
import kotlinx.android.synthetic.main.deck_fragment.*
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

class DeckFragment : Fragment() {

    companion object {
        fun newInstance() =
            DeckFragment()
    }

    private lateinit var viewModel: SharedViewModel
    private val searchAdapter = SearchAdapter { card: Card -> cardItemClicked(card) }
    private val deckAdapter = DeckAdapter { card: CardCount -> deckItemClicked(card) }
    val args: DeckFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.let {
            viewModel = ViewModelProvider(it).get((SharedViewModel::class.java))
        }
        val view: View = inflater.inflate(R.layout.deck_fragment, container, false)
        val parent = view.findViewById<ConstraintLayout>(R.id.deck_fragment)
        val deckRV = view.findViewById<RecyclerView>(R.id.deck_rv)
        val editBtn = view.findViewById<ToggleButton>(R.id.edit_deck_btn)
        val searchRV = view.findViewById<RecyclerView>(R.id.search_rv)
        val filterBtn = view.findViewById<Button>(R.id.filter_btn)
        val deckName = view.findViewById<TextView>(R.id.deck_name_tv)
        deckRV.layoutManager = LinearLayoutManager(requireContext())
        deckRV.adapter = deckAdapter
        searchRV.layoutManager = LinearLayoutManager(requireContext())
        searchRV.adapter = searchAdapter

        val deckChart = view.findViewById<BarChart>(R.id.mana_chart)
        viewModel.setDeck(args.deckId)
        viewModel.mCurrentDeck.observe(viewLifecycleOwner, Observer { cards ->
            cards.let {
                val cardList = it?.cards!!.sortedWith(compareBy { cardCount ->
                    cardCount.card.cmc
                })
                deckAdapter.loadCards(cardList)
                viewModel.drawChart(deckChart, it.cards)
                deckName.text = it.deck.name
//                if(it.deck.cIdentity.isNullOrEmpty()){
//                    initSearch("c:W")
//                } else {
//                    initSearch("c:${it.deck.cIdentity}")
//                }
            }
        })
        Log.d("debug", "deckfrgment deck id${viewModel.mCurrentDeck.value?.deck?.deckId}")

        val constraintSet1 = ConstraintSet()
        constraintSet1.clone(parent)
        constraintSet1.setVisibility(R.id.search_rv, View.VISIBLE)
        constraintSet1.connect(R.id.deck_rv, ConstraintSet.BOTTOM, R.id.search_rv, ConstraintSet.TOP)
        val constraintSet2 = ConstraintSet()
        constraintSet2.clone(parent)
        constraintSet2.setVisibility(R.id.search_rv, View.GONE)
        constraintSet2.connect(R.id.deck_rv, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)


        editBtn.setOnCheckedChangeListener { _, isChecked ->
            TransitionManager.beginDelayedTransition(parent)
            val constraint = if (isChecked) constraintSet1 else constraintSet2
            constraint.applyTo(parent)
        }

        filterBtn.setOnClickListener {
            cardFilter()
        }
        loadUI(view)
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        deck_rv.adapter = null
    }
//
//    override fun onStart() {
//        super.onStart()
//        viewModel = ViewModelProvider(this).get((SharedViewModel::class.java))
//        deck_rv.adapter = DeckAdapter { cardCount: CardCount -> deckItemClicked(cardCount) }
//        deck_rv.layoutManager = LinearLayoutManager(requireContext())
//    }

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
        val config = PagedList.Config.Builder()
            .setPageSize(175)
            .setEnablePlaceholders(false)
            .build()
        viewModel.mLiveData = initPagedListBuilder(config, query).build()

        viewModel.mLiveData.observe(viewLifecycleOwner, Observer{ pagedList ->
            searchAdapter.submitList(pagedList)
        })
    }

    private fun initPagedListBuilder(
        config: PagedList.Config,
        query: String
    ): LivePagedListBuilder<String, Card> {
        return LivePagedListBuilder(CardSearchDataSourceFactory(query), config)
    }

    private fun makeAlertDialog(cardItem: CardCount) {
        // inflate popup view
        val view = LayoutInflater.from(activity).inflate(R.layout.deck_card_popup, null)
        val et = view.findViewById<EditText>(R.id.card_count_et)
        val closeButton = view.findViewById<Button>(R.id.dc_popup_close_btn)
        val artButton = view.findViewById<Button>(R.id.dc_popup_art_btn)
        val deckBg = view.findViewById<ImageView>(R.id.deck_background_iv)

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
            val deck = viewModel.mCurrentDeck.value?.deck!!
            Log.d("salami", "${deck.name} art button clicked")
            val deckIV = requireActivity().findViewById<ImageView>(R.id.deck_background_iv)
            viewModel.updateDeckBackground(deck, cardItem)
            popupWindow.dismiss()
        }

        // dismiss listener
        popupWindow.setOnDismissListener {
            //            listAdapter.notifyDataSetChanged()
        }
        //show popop window
        popupWindow.showAtLocation(view, Gravity.TOP, 0, 0)

    }

    private fun cardFilter() {
        // inflate popup view
        val view by lazy { LayoutInflater.from(activity).inflate(R.layout.filters_popup, null) }
        val wBtn = view.findViewById<ToggleButton>(R.id.w_btn)
        val wFrame = view.findViewById<FrameLayout>(R.id.w_frame)
        val rBtn = view.findViewById<ToggleButton>(R.id.r_btn)
        val rFrame = view.findViewById<FrameLayout>(R.id.r_frame)
        val bBtn = view.findViewById<ToggleButton>(R.id.b_btn)
        val bFrame = view.findViewById<FrameLayout>(R.id.b_frame)
        val uBtn = view.findViewById<ToggleButton>(R.id.u_btn)
        val uFrame = view.findViewById<FrameLayout>(R.id.u_frame)
        val gBtn = view.findViewById<ToggleButton>(R.id.g_btn)
        val gFrame = view.findViewById<FrameLayout>(R.id.g_frame)
        val cBtn = view.findViewById<ToggleButton>(R.id.c_btn)
        val cFrame = view.findViewById<FrameLayout>(R.id.c_frame)
        val oneBtn = view.findViewById<ToggleButton>(R.id.one_btn)
        val oneFrame = view.findViewById<FrameLayout>(R.id.one_frame)
        val twoBtn = view.findViewById<ToggleButton>(R.id.two_btn)
        val twoFrame = view.findViewById<FrameLayout>(R.id.two_frame)
        val threeBtn = view.findViewById<ToggleButton>(R.id.three_btn)
        val threeFrame = view.findViewById<FrameLayout>(R.id.three_frame)
        val fourBtn = view.findViewById<ToggleButton>(R.id.four_btn)
        val fourFrame = view.findViewById<FrameLayout>(R.id.four_frame)
        val fiveBtn = view.findViewById<ToggleButton>(R.id.five_btn)
        val fiveFrame = view.findViewById<FrameLayout>(R.id.five_frame)
        val sixBtn = view.findViewById<ToggleButton>(R.id.sixplus_btn)
        val sixFrame = view.findViewById<FrameLayout>(R.id.sixplus_frame)
        val creatureBtn = view.findViewById<ToggleButton>(R.id.creature_btn)
        val planeswalkerBtn = view.findViewById<ToggleButton>(R.id.planeswalker_btn)
        val instantBtn = view.findViewById<ToggleButton>(R.id.instant_btn)
        val sorceryBtn = view.findViewById<ToggleButton>(R.id.sorcery_btn)
        val enchantmentBtn = view.findViewById<ToggleButton>(R.id.enchantment_btn)
        val artifactBtn = view.findViewById<ToggleButton>(R.id.artifact_btn)
        val landBtn = view.findViewById<ToggleButton>(R.id.land_btn)
        val commanderBtn = view.findViewById<ToggleButton>(R.id.commander_btn)
        val checkBtn = view.findViewById<Button>(R.id.check_btn)

        val popupWindow = PopupWindow(
            view, // Custom view to show in popup window
            LinearLayout.LayoutParams.WRAP_CONTENT, // Width of popup window
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )
        //onChecked listeners for filter buttons
        wBtn.setOnCheckedChangeListener { _, isChecked ->
            viewModel.manaString = viewModel.filterMana(isChecked, viewModel.manaList, "W", wFrame)
        }
        rBtn.setOnCheckedChangeListener { _, isChecked ->
            viewModel.manaString = viewModel.filterMana(isChecked, viewModel.manaList, "R", rFrame)
        }
        bBtn.setOnCheckedChangeListener { _, isChecked ->
            viewModel.manaString = viewModel.filterMana(isChecked, viewModel.manaList, "B", bFrame)
        }
        uBtn.setOnCheckedChangeListener { _, isChecked ->
            viewModel.manaString = viewModel.filterMana(isChecked, viewModel.manaList, "U", uFrame)
        }
        gBtn.setOnCheckedChangeListener { _, isChecked ->
            viewModel.manaString = viewModel.filterMana(isChecked, viewModel.manaList, "G", gFrame)
        }
        cBtn.setOnCheckedChangeListener { _, isChecked ->
            viewModel.manaString = viewModel.filterMana(isChecked, viewModel.manaList, "C", cFrame)
        }
        oneBtn.setOnCheckedChangeListener { _, isChecked ->
            viewModel.cmcString = viewModel.filterCMC(isChecked, viewModel.cmcList, 1, oneFrame)
        }
        twoBtn.setOnCheckedChangeListener { _, isChecked ->
            viewModel.cmcString = viewModel.filterCMC(isChecked, viewModel.cmcList, 2, twoFrame)
        }
        threeBtn.setOnCheckedChangeListener { _, isChecked ->
            viewModel.cmcString = viewModel.filterCMC(isChecked, viewModel.cmcList, 3, threeFrame)
        }
        fourBtn.setOnCheckedChangeListener { _, isChecked ->
            viewModel.cmcString = viewModel.filterCMC(isChecked, viewModel.cmcList, 4, fourFrame)
        }
        fiveBtn.setOnCheckedChangeListener { _, isChecked ->
            viewModel.cmcString = viewModel.filterCMC(isChecked, viewModel.cmcList, 5, fiveFrame)
        }
        sixBtn.setOnCheckedChangeListener { _, isChecked ->
            viewModel.cmcString = viewModel.filterCMC(isChecked, viewModel.cmcList, 6, sixFrame)
        }
        creatureBtn.setOnCheckedChangeListener { _, isChecked ->
            viewModel.typeString = viewModel.filterType(isChecked, viewModel.typeList, "creature", creatureBtn)
        }
        planeswalkerBtn.setOnCheckedChangeListener { _, isChecked ->
            viewModel.typeString = viewModel.filterType(isChecked, viewModel.typeList, "planeswalker", planeswalkerBtn)
        }
        instantBtn.setOnCheckedChangeListener { _, isChecked ->
            viewModel.typeString = viewModel.filterType(isChecked, viewModel.typeList, "instant", instantBtn)
        }

        sorceryBtn.setOnCheckedChangeListener { _, isChecked ->
            viewModel.typeString = viewModel.filterType(isChecked, viewModel.typeList, "sorcery", sorceryBtn)
        }
        enchantmentBtn.setOnCheckedChangeListener { _, isChecked ->
            viewModel.typeString = viewModel.filterType(isChecked, viewModel.typeList, "enchantment", enchantmentBtn)
        }

        artifactBtn.setOnCheckedChangeListener { _, isChecked ->
            viewModel.typeString = viewModel.filterType(isChecked, viewModel.typeList, "artifact", artifactBtn)
        }
        landBtn.setOnCheckedChangeListener { _, isChecked ->
            viewModel.typeString = viewModel.filterType(isChecked, viewModel.typeList, "land", landBtn)
        }

        commanderBtn.setOnCheckedChangeListener { _, isChecked ->
            viewModel.typeString = viewModel.filterType(isChecked, viewModel.typeList, "commander", commanderBtn)
        }

        checkBtn.setOnClickListener {
            val query = "${viewModel.manaString}+${viewModel.cmcString}+${viewModel.typeString}+f:standard"
            Log.d("debug", query)
            initSearch(query)
//            viewModel.typeList.clear()
//            viewModel.manaList.clear()
//            viewModel.cmcList.clear()
            viewModel.manaString = ""
            viewModel.typeString = ""
            viewModel.cmcString = ""
        }

        // dismiss listener
        popupWindow.setOnDismissListener {
            //            listAdapter.notifyDataSetChanged()
        }
        //show popop window
        popupWindow.showAtLocation(view, Gravity.TOP, 0, 0)
    }

    private fun loadUI(view:View){
        val progressBar = view.findViewById<ProgressBar>(R.id.progress_bar)
        val background = view.findViewById<View>(R.id.fullscreen_view)
        val time: Long = Random.nextLong(1000, 2500)
        Handler().postDelayed(
            {
                progressBar.visibility = View.GONE
                background.visibility = View.GONE
            },
            time
        )

    }
}
