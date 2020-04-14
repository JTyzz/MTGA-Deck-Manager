package com.jtyzzer.vantress.fragments

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import com.bumptech.glide.Glide
import com.jtyzzer.vantress.R
import com.jtyzzer.vantress.adapter.DeckAdapter
import com.jtyzzer.vantress.adapter.SearchAdapter
import com.jtyzzer.vantress.model.Card
import com.jtyzzer.vantress.model.CardCount
import com.jtyzzer.vantress.network.CardSearchDataSourceFactory
import com.jtyzzer.vantress.utilities.ImageStoreManager
import com.github.mikephil.charting.charts.BarChart
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.deck_fragment.*
import kotlinx.coroutines.runBlocking

class DeckFragment : Fragment() {

    companion object {
        fun newInstance() =
            DeckFragment()
    }

    private lateinit var viewModel: SharedViewModel
    private val searchAdapter = SearchAdapter { card: Card -> cardItemClicked(card) }
    private val deckAdapter = DeckAdapter { card: CardCount -> deckItemClicked(card) }
    private val args: DeckFragmentArgs by navArgs()

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
        val addCardsBtn = view.findViewById<ToggleButton>(R.id.addcards_btn)
        val searchRV = view.findViewById<RecyclerView>(R.id.search_rv)
        val filterBtn = view.findViewById<FloatingActionButton>(R.id.filter_fab)
        val deckName = view.findViewById<TextView>(R.id.deck_name_tv)
        val editDeckName = view.findViewById<EditText>(R.id.deck_name_et)
        val deckBackground = view.findViewById<ImageView>(R.id.deck_background_iv)

        loadUI()

        deckRV.layoutManager = LinearLayoutManager(requireContext())
        deckRV.adapter = deckAdapter
        searchRV.layoutManager = GridLayoutManager(requireContext(), 2)
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
                editDeckName.hint = it.deck.name
                try {
                    val bitmap = ImageStoreManager
                        .getImageFromInternalStorage(requireContext(), it.deck.uri!!)
                    Glide.with(requireContext())
                        .load(bitmap)
                        .into(deckBackground)
                } catch (e: Exception) {
                    Log.d("debug", "listadapter $e")
                }
            }
        })

        deckName.setOnClickListener {
            deckName.visibility = View.INVISIBLE
            editDeckName.visibility = View.VISIBLE
            editDeckName.requestFocus()
            editDeckName.isFocusableInTouchMode

        }

        editDeckName.setOnEditorActionListener { _, actionId, event ->
            if ((event != null && (event.keyCode == KeyEvent.KEYCODE_ENTER ||
                        event.keyCode == KeyEvent.KEYCODE_BACK)) ||
                (actionId == EditorInfo.IME_ACTION_DONE)
            ) {
                viewModel.updateDeckName(
                    viewModel.mCurrentDeck.value?.deck!!,
                    editDeckName.text.toString()
                )
                Handler().postDelayed(
                    {
                        editDeckName.visibility = View.INVISIBLE
                        deckName.visibility = View.VISIBLE
                    }, 1000
                )
            }
            false
        }
        //reveals search recyclerview and filter button
        val constraintSet1 = ConstraintSet()
        constraintSet1.clone(parent)
        constraintSet1.setVisibility(R.id.search_rv, View.VISIBLE)
        constraintSet1.setVisibility(R.id.filter_fab, View.VISIBLE)
        constraintSet1.connect(
            R.id.deck_rv,
            ConstraintSet.BOTTOM,
            R.id.search_rv,
            ConstraintSet.TOP
        )
        //hides search recyclerview and filter button
        val constraintSet2 = ConstraintSet()
        constraintSet2.clone(parent)
        constraintSet2.setVisibility(R.id.search_rv, View.GONE)
        constraintSet2.setVisibility(R.id.filter_fab, View.GONE)
        constraintSet2.connect(
            R.id.deck_rv,
            ConstraintSet.BOTTOM,
            ConstraintSet.PARENT_ID,
            ConstraintSet.BOTTOM
        )

        addCardsBtn.setOnCheckedChangeListener { v, isChecked ->
            TransitionManager.beginDelayedTransition(parent)
            if (isChecked) {
                constraintSet1.applyTo(parent)
                v.background = requireActivity().getDrawable(R.drawable.btn_selected)
                v.setTextColor(ContextCompat.getColor(requireActivity(), R.color.bar))
            } else if (!isChecked) {
                constraintSet2.applyTo(parent)
                v.background = requireActivity().getDrawable(R.drawable.btn_unselected)
                v.setTextColor(ContextCompat.getColor(requireActivity(), R.color.rose))
            }
        }

        filterBtn.setOnClickListener {
            cardFilter()
        }

        return view
    }

    //sets adapter to null to avoid memory leak
    override fun onDestroyView() {
        super.onDestroyView()
        deck_rv.adapter = null
        search_rv.adapter = null
    }

    private fun deckItemClicked(item: CardCount) {
        cardEdit(item)
    }

    private fun cardItemClicked(item: Card) {
        cardAdd(item)
    }

    //Paginated search
    private fun initSearch(query: String) {
        val config = PagedList.Config.Builder()
            .setPageSize(175)
            .setEnablePlaceholders(false)
            .build()
        viewModel.mLiveData = initPagedListBuilder(config, query).build()

        viewModel.mLiveData.observe(viewLifecycleOwner, Observer { pagedList ->
            searchAdapter.submitList(pagedList)
        })
    }

    private fun initPagedListBuilder(
        config: PagedList.Config,
        query: String
    ): LivePagedListBuilder<String, Card> {
        return LivePagedListBuilder(CardSearchDataSourceFactory(query), config)
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
        val groupButtonsToggle = arrayOf(
            oneBtn, twoBtn, threeBtn, fourBtn,
            fiveBtn, sixBtn
        )

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
        oneBtn.setOnCheckedChangeListener { v, isChecked ->
            if (isChecked) groupButtonsToggle.forEach { if (it != v) it.isChecked = false }
            viewModel.cmcString = viewModel.filterCMC(isChecked, viewModel.cmcList, 1, oneFrame)
        }
        twoBtn.setOnCheckedChangeListener { v, isChecked ->
            if (isChecked) groupButtonsToggle.forEach { if (it != v) it.isChecked = false }
            viewModel.cmcString = viewModel.filterCMC(isChecked, viewModel.cmcList, 2, twoFrame)
        }
        threeBtn.setOnCheckedChangeListener { v, isChecked ->
            if (isChecked) groupButtonsToggle.forEach { if (it != v) it.isChecked = false }
            viewModel.cmcString = viewModel.filterCMC(isChecked, viewModel.cmcList, 3, threeFrame)
        }
        fourBtn.setOnCheckedChangeListener { v, isChecked ->
            if (isChecked) groupButtonsToggle.forEach { if (it != v) it.isChecked = false }
            viewModel.cmcString = viewModel.filterCMC(isChecked, viewModel.cmcList, 4, fourFrame)
        }
        fiveBtn.setOnCheckedChangeListener { v, isChecked ->
            if (isChecked) groupButtonsToggle.forEach { if (it != v) it.isChecked = false }
            viewModel.cmcString = viewModel.filterCMC(isChecked, viewModel.cmcList, 5, fiveFrame)
        }
        sixBtn.setOnCheckedChangeListener { v, isChecked ->
            if (isChecked) groupButtonsToggle.forEach { if (it != v) it.isChecked = false }
            viewModel.cmcString = viewModel.filterCMC(isChecked, viewModel.cmcList, 6, sixFrame)
        }
        creatureBtn.setOnCheckedChangeListener { _, isChecked ->
            viewModel.typeString =
                viewModel.filterType(isChecked, viewModel.typeList, "creature", creatureBtn)
        }
        planeswalkerBtn.setOnCheckedChangeListener { _, isChecked ->
            viewModel.typeString =
                viewModel.filterType(isChecked, viewModel.typeList, "planeswalker", planeswalkerBtn)
        }
        instantBtn.setOnCheckedChangeListener { _, isChecked ->
            viewModel.typeString =
                viewModel.filterType(isChecked, viewModel.typeList, "instant", instantBtn)
        }

        sorceryBtn.setOnCheckedChangeListener { _, isChecked ->
            viewModel.typeString =
                viewModel.filterType(isChecked, viewModel.typeList, "sorcery", sorceryBtn)
        }
        enchantmentBtn.setOnCheckedChangeListener { _, isChecked ->
            viewModel.typeString =
                viewModel.filterType(isChecked, viewModel.typeList, "enchantment", enchantmentBtn)
        }

        artifactBtn.setOnCheckedChangeListener { _, isChecked ->
            viewModel.typeString =
                viewModel.filterType(isChecked, viewModel.typeList, "artifact", artifactBtn)
        }
        landBtn.setOnCheckedChangeListener { _, isChecked ->
            viewModel.typeString =
                viewModel.filterType(isChecked, viewModel.typeList, "land", landBtn)
        }

        commanderBtn.setOnCheckedChangeListener { _, isChecked ->
            viewModel.typeString =
                viewModel.filterType(isChecked, viewModel.typeList, "commander", commanderBtn)
        }

        // dismiss listener
        popupWindow.setOnDismissListener {
            val query =
                "${viewModel.manaString}+${viewModel.cmcString}+${viewModel.typeString}+f:standard"
            initSearch(query)
            viewModel.typeList.clear()
            viewModel.manaList.clear()
            viewModel.cmcList.clear()
            viewModel.manaString = ""
            viewModel.typeString = ""
            viewModel.cmcString = ""
        }
        //show popop window
        popupWindow.showAtLocation(view, Gravity.TOP, 0, 0)
    }

    private fun cardAdd(card: Card) {
        val view by lazy { LayoutInflater.from(activity).inflate(R.layout.add_card_popup, null) }
        val addBtn = view.findViewById<ImageView>(R.id.ac_increase_btn)
        val cardIV = view.findViewById<ImageView>(R.id.add_card_iv)
        val minusBtn = view.findViewById<ImageView>(R.id.ac_minus_btn)
        val oneIV = view.findViewById<ImageView>(R.id.counter_one_iv)
        val twoIV = view.findViewById<ImageView>(R.id.counter_two_iv)
        val threeIV = view.findViewById<ImageView>(R.id.counter_three_iv)
        val fourIV = view.findViewById<ImageView>(R.id.counter_four_iv)
        var cCount = 0

        val popupWindow = PopupWindow(
            view, // Custom view to show in popup window
            LinearLayout.LayoutParams.WRAP_CONTENT, // Width of popup window
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )

        Glide.with(requireContext()).load(card.imageUris?.normal).into(cardIV)
        cardIV.clipToOutline = true

        viewModel.mCurrentDeck.observe(viewLifecycleOwner, Observer {
            if (it.cards.isNullOrEmpty()) {
            } else {
                for (item in it.cards) {
                    if (item.id == card.cardId) {
                        cCount = item.count
                        when (item.count) {
                            1 -> {
                                oneIV.setImageDrawable(requireActivity().getDrawable(R.drawable.counter_on))
                                twoIV.setImageDrawable(requireActivity().getDrawable(R.drawable.counter_off))
                                threeIV.setImageDrawable(requireActivity().getDrawable(R.drawable.counter_off))
                                fourIV.setImageDrawable(requireActivity().getDrawable(R.drawable.counter_off))
                            }
                            2 -> {
                                oneIV.setImageDrawable(requireActivity().getDrawable(R.drawable.counter_on))
                                twoIV.setImageDrawable(requireActivity().getDrawable(R.drawable.counter_on))
                                threeIV.setImageDrawable(requireActivity().getDrawable(R.drawable.counter_off))
                                fourIV.setImageDrawable(requireActivity().getDrawable(R.drawable.counter_off))
                            }
                            3 -> {
                                oneIV.setImageDrawable(requireActivity().getDrawable(R.drawable.counter_on))
                                twoIV.setImageDrawable(requireActivity().getDrawable(R.drawable.counter_on))
                                threeIV.setImageDrawable(requireActivity().getDrawable(R.drawable.counter_on))
                                fourIV.setImageDrawable(requireActivity().getDrawable(R.drawable.counter_off))
                            }
                            4 -> {
                                oneIV.setImageDrawable(requireActivity().getDrawable(R.drawable.counter_on))
                                twoIV.setImageDrawable(requireActivity().getDrawable(R.drawable.counter_on))
                                threeIV.setImageDrawable(requireActivity().getDrawable(R.drawable.counter_on))
                                fourIV.setImageDrawable(requireActivity().getDrawable(R.drawable.counter_on))
                            }
                            else -> {
                                oneIV.setImageDrawable(requireActivity().getDrawable(R.drawable.counter_off))
                                twoIV.setImageDrawable(requireActivity().getDrawable(R.drawable.counter_off))
                                threeIV.setImageDrawable(requireActivity().getDrawable(R.drawable.counter_off))
                                fourIV.setImageDrawable(requireActivity().getDrawable(R.drawable.counter_off))
                            }
                        }
                    }
                }
            }
        })

        addBtn.setOnClickListener {
            if (cCount in 0..3) {
                cCount++
                runBlocking {
                    viewModel.insertCardtoDeck(card, cCount)
                }
            }
        }

        minusBtn.setOnClickListener {
            if (cCount in 1..4) {
                cCount--
                runBlocking {
                    if (cCount == 0) {
                        viewModel.deleteCard(card.cardId)
                    } else {
                        viewModel.insertCardtoDeck(card, cCount)
                    }

                }
            }
        }
        popupWindow.setOnDismissListener {
        }
        popupWindow.showAtLocation(view, Gravity.TOP, 0, 0)
    }

    private fun cardEdit(card: CardCount) {
        val view by lazy { LayoutInflater.from(activity).inflate(R.layout.edit_card_popup, null) }
        val addBtn = view.findViewById<ImageView>(R.id.ec_increase_btn)
        val cardIV = view.findViewById<ImageView>(R.id.edit_card_iv)
        val minusBtn = view.findViewById<ImageView>(R.id.ec_minus_btn)
        val oneIV = view.findViewById<ImageView>(R.id.ec_counter_one_iv)
        val twoIV = view.findViewById<ImageView>(R.id.ec_counter_two_iv)
        val threeIV = view.findViewById<ImageView>(R.id.ec_counter_three_iv)
        val fourIV = view.findViewById<ImageView>(R.id.ec_counter_four_iv)
        val backgroundBtn = view.findViewById<Button>(R.id.background_btn)
        var cCount = 0

        val popupWindow = PopupWindow(
            view, // Custom view to show in popup window
            LinearLayout.LayoutParams.WRAP_CONTENT, // Width of popup window
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )

        Glide.with(requireContext()).load(card.card.imageUris?.normal).into(cardIV)
        cardIV.clipToOutline = true

        viewModel.mCurrentDeck.observe(viewLifecycleOwner, Observer {
            if (it.cards.isNullOrEmpty()) {
            } else {
                for (item in it.cards) {
                    if (item.id == card.card.cardId) {
                        cCount = item.count
                        when (item.count) {
                            1 -> {
                                oneIV.setImageDrawable(requireActivity().getDrawable(R.drawable.counter_on))
                                twoIV.setImageDrawable(requireActivity().getDrawable(R.drawable.counter_off))
                                threeIV.setImageDrawable(requireActivity().getDrawable(R.drawable.counter_off))
                                fourIV.setImageDrawable(requireActivity().getDrawable(R.drawable.counter_off))
                            }
                            2 -> {
                                oneIV.setImageDrawable(requireActivity().getDrawable(R.drawable.counter_on))
                                twoIV.setImageDrawable(requireActivity().getDrawable(R.drawable.counter_on))
                                threeIV.setImageDrawable(requireActivity().getDrawable(R.drawable.counter_off))
                                fourIV.setImageDrawable(requireActivity().getDrawable(R.drawable.counter_off))
                            }
                            3 -> {
                                oneIV.setImageDrawable(requireActivity().getDrawable(R.drawable.counter_on))
                                twoIV.setImageDrawable(requireActivity().getDrawable(R.drawable.counter_on))
                                threeIV.setImageDrawable(requireActivity().getDrawable(R.drawable.counter_on))
                                fourIV.setImageDrawable(requireActivity().getDrawable(R.drawable.counter_off))
                            }
                            4 -> {
                                oneIV.setImageDrawable(requireActivity().getDrawable(R.drawable.counter_on))
                                twoIV.setImageDrawable(requireActivity().getDrawable(R.drawable.counter_on))
                                threeIV.setImageDrawable(requireActivity().getDrawable(R.drawable.counter_on))
                                fourIV.setImageDrawable(requireActivity().getDrawable(R.drawable.counter_on))
                            }
                            else -> {
                                oneIV.setImageDrawable(requireActivity().getDrawable(R.drawable.counter_off))
                                twoIV.setImageDrawable(requireActivity().getDrawable(R.drawable.counter_off))
                                threeIV.setImageDrawable(requireActivity().getDrawable(R.drawable.counter_off))
                                fourIV.setImageDrawable(requireActivity().getDrawable(R.drawable.counter_off))
                            }
                        }
                    }
                }
            }
        })

        addBtn.setOnClickListener {
            if (cCount in 0..3) {
                cCount++
                runBlocking {
                    viewModel.insertCardtoDeck(card.card, cCount)
                }
            }
        }

        minusBtn.setOnClickListener {
            if (cCount in 1..4) {
                cCount--
                runBlocking {
                    if (cCount == 0) {
                        viewModel.deleteCard(card.card.cardId)
                    } else {
                        viewModel.insertCardtoDeck(card.card, cCount)
                    }

                }
            }
        }
        backgroundBtn.setOnClickListener {
            val deck = viewModel.mCurrentDeck.value?.deck!!
            viewModel.updateDeckBackground(deck, card)
        }

        popupWindow.setOnDismissListener {
        }
        popupWindow.showAtLocation(view, Gravity.TOP, 0, 0)
    }

    private fun loadUI() {
        if (viewModel.mCurrentDeck.value?.deck?.cIdentity.isNullOrEmpty()) {
            initSearch("c:W+f:standard")
        } else {
            initSearch("c:${viewModel.mCurrentDeck.value?.deck!!.cIdentity}+f:standard")
        }
    }
}
