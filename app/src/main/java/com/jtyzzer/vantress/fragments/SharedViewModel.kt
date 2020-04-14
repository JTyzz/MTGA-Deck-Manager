package com.jtyzzer.vantress.fragments

import android.app.Application
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.FrameLayout
import android.widget.ToggleButton
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import androidx.paging.PagedList
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.jtyzzer.vantress.R
import com.jtyzzer.vantress.database.AppDatabase
import com.jtyzzer.vantress.database.DeckRepo
import com.jtyzzer.vantress.model.*
import com.jtyzzer.vantress.utilities.ImageStoreManager
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import kotlinx.coroutines.*

class SharedViewModel(application: Application) : AndroidViewModel(application) {
    private val dbRepo: DeckRepo
    private val context = application.applicationContext

    lateinit var mLiveData: LiveData<PagedList<Card>>
    lateinit var mCurrentDeck: LiveData<DeckWithCards>


    var deckNamesLD: LiveData<List<String>>
    var deckNames: MutableList<String> = mutableListOf()
    var allLDDecks: LiveData<List<DeckWithCards>>

    val manaList = mutableListOf<String>()
    val cmcList = mutableListOf<String>()
    val typeList = mutableListOf<String>()
    var typeString = String()
    var manaString = String()
    var cmcString = String()


    init {
        //get reference to deckdao from appdatabase to construct deckrepo
        val deckDao = AppDatabase.getDatabase(application, viewModelScope).deckDao()
        dbRepo = DeckRepo(deckDao)
        allLDDecks = dbRepo.allLDDecks
        deckNamesLD = dbRepo.deckNames
    }
    /* LIST FRAGMENT FUNCTIONS*/

    //Adds deck named New Deck and returns DeckWithCards livedata
    suspend fun insertDeck(name: String): Long {
        val deckInput = Deck(name, System.currentTimeMillis())
        checkExistingName(deckInput)
        return dbRepo.setNewDeck(deckInput)
    }


    fun setDeck(deckId: Long) = viewModelScope.launch {
        mCurrentDeck = dbRepo.getDeckById(deckId)
    }

    //checks name of deck and increments name by 1 if it exists
    private fun checkExistingName(deck: Deck): Deck {
        var deckName = deck.name
        var count = 0
        fun checkNames() {
            if (deckNamesLD.value!!.contains(deckName)) {
                count++
                if (deckNamesLD.value!!.contains(deckName.plus(count))) {
                    checkNames()
                }
            }
        }
        checkNames()
        if (count != 0) {
            deckName = deckName.plus(count)
            deck.name = deckName
        }
        return deck
    }

    /* DECK FRAGMENT FUNCTIONS */

    fun deleteDeckById() = viewModelScope.launch {
        if (mCurrentDeck.value?.cards.isNullOrEmpty()) {
            dbRepo.deleteDeckById(mCurrentDeck.value?.deck?.deckId!!)
        }
    }

    suspend fun insertCardtoDeck(card: Card, count: Int) = viewModelScope.launch {
        val currentDeck = mCurrentDeck.value!!.deck
        val cardCount = CardCount(card.cardId, count, card)
        dbRepo.insertCardCount(cardCount)
        val junction = DeckCardJoin(cardCount.id, currentDeck.deckId!!)
        dbRepo.insertRelation(junction)
        if (currentDeck.cIdentity.isNullOrEmpty()) {
            updateDeckColorId(currentDeck.deckId!!, card.colors!!.joinToString(","))
        } else {
            val newDeckColors = checkDeckColorId(card, currentDeck)
            if (currentDeck.cIdentity != newDeckColors) {
                updateDeckColorId(currentDeck.deckId!!, newDeckColors)
            }
        }
    }

    fun deleteCard(cardId: String) = viewModelScope.launch {
        dbRepo.deleteCard(cardId)
    }

    private fun updateDeckColorId(deckId: Long, colorId: String) = viewModelScope.launch {
        dbRepo.updateDeckColorId(deckId, colorId)
    }

    fun updateDeckBackground(deck: Deck, card: CardCount) {
        runBlocking {
            val res = async { saveDeckBackground(deck, card) }
            res.await()
            dbRepo.updateDeckBackground(deck.deckId!!, card.card.cardId)
        }
    }

    fun updateDeckName(deck: Deck, name: String) {
        runBlocking {
            dbRepo.updateDeckName(deck.deckId!!, name)
        }
    }

    private fun saveDeckBackground(deck: Deck, card: CardCount) = viewModelScope.launch {
        Log.d("salami", "${deck.name} updating background")
        Glide.with(context)
            .asBitmap()
            .load(card.card.imageUris?.artCrop)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    ImageStoreManager.saveToInternalStorage(
                        context,
                        resource,
                        card.card.cardId
                    )
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })
    }

    private fun checkDeckColorId(card: Card, deck: Deck): String {
        val cardColor = card.colors
        val deckColor = deck.cIdentity?.split(",")?.toMutableList()

        for (item in cardColor!!.iterator()) {
            if (deckColor?.contains(item)!!) {
                continue
            } else {
                deckColor.add(item)
            }
        }
        val sortedDeckColor = deckColor?.apply {
            sort()
        }
        Log.d("debug", "sorted ${sortedDeckColor?.joinToString(",")!!}")
        return sortedDeckColor.joinToString(",")
    }

    fun drawChart(chart: BarChart, cardList: List<CardCount>) {
        val cmc1 = maxOf(cardList.count { it.card.cmc == 1 }.toFloat(), 0f)
        val cmc2 = maxOf(cardList.count { it.card.cmc == 2 }.toFloat(), 0f)
        val cmc3 = maxOf(cardList.count { it.card.cmc == 3 }.toFloat(), 0f)
        val cmc4 = maxOf(cardList.count { it.card.cmc == 4 }.toFloat(), 0f)
        val cmc5 = maxOf(cardList.count { it.card.cmc == 5 }.toFloat(), 0f)
        val cmc6 = maxOf(cardList.count { it.card.cmc!! >= 6 }.toFloat(), 0f)
        val cmcList = listOf(cmc1, cmc2, cmc3, cmc4, cmc5, cmc6)

        chart.setDrawBarShadow(false)
        chart.setDrawValueAboveBar(true)
        val description = Description()
        description.text = ""
        chart.description = description
        chart.setPinchZoom(false)
        chart.setDrawGridBackground(false)
        chart.legend.isEnabled = false

        val x1: XAxis = chart.xAxis
        x1.granularity = 1f
        x1.setCenterAxisLabels(false)
        x1.setDrawGridLines(false)
        x1.isEnabled = false

        val leftAxis: YAxis = chart.axisLeft
        leftAxis.setDrawGridLines(false)
        leftAxis.setDrawTopYLabelEntry(false)
        leftAxis.axisMinimum = 0f
        leftAxis.isEnabled = false
        leftAxis.axisMaximum = maxOf(cmcList.max()!!, 1f)
        chart.axisRight.isEnabled = false

        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(0f, cmc1))
        entries.add(BarEntry(1f, cmc2))
        entries.add(BarEntry(2f, cmc3))
        entries.add(BarEntry(3f, cmc4))
        entries.add(BarEntry(4f, cmc5))
        entries.add(BarEntry(5f, cmc6))


        val set = BarDataSet(entries, "CMCDataSet")
        set.color = getApplication<Application>().resources.getColor(R.color.bar, null)
        set.setDrawValues(false)
        set.setDrawIcons(false)
        set.barShadowColor =
            getApplication<Application>().resources.getColor(R.color.extradark, null)
        val data = BarData(set)

        data.barWidth = 0.5f
        chart.data = data
        chart.setDrawBarShadow(true)
        chart.invalidate()
    }

    //for filterpopup in deckfragment, returns search query for mana colors
    fun filterMana(
        isChecked: Boolean,
        manaList: MutableList<String>,
        s: String,
        frame: FrameLayout
    ): String {
        if (isChecked) {
            if (!manaList.contains(s)) {
                manaList.add(s)
            }
            frame.background =
                getApplication<Application>().resources.getDrawable(R.drawable.item_selected, null)
        } else if (!isChecked) {
            if (manaList.contains(s)) {
                manaList.remove(s)
                frame.background = getApplication<Application>().resources.getDrawable(
                    R.drawable.item_unselected,
                    null
                )
            }
        }
        return "c:${manaList.joinToString("")}"
    }

    //for filterpopup in deckfragment, returns search query for mana cost
    fun filterCMC(
        isChecked: Boolean,
        cmcList: MutableList<String>,
        i: Int,
        frame: FrameLayout
    ): String {
        if (isChecked) {
            if (!cmcList.contains("cmc>$i") || !cmcList.contains("cmc:$i")) {
                frame.background = getApplication<Application>().resources.getDrawable(
                    R.drawable.item_selected,
                    null
                )
                if (i == 6) {
                    cmcList.add("cmc>$i")
                } else {
                    cmcList.add("cmc:$i")
                }
                frame.background = getApplication<Application>().resources.getDrawable(
                    R.drawable.item_selected,
                    null
                )
            }
        } else if (!isChecked) {
            if (cmcList.contains("cmc:$i") || cmcList.contains("cmc>$i")) {
                if (i == 6) {
                    cmcList.remove("cmc>$i")
                } else {
                    cmcList.remove("cmc:$i")
                }
                frame.background = getApplication<Application>().resources.getDrawable(
                    R.drawable.item_unselected,
                    null
                )
            }
        }
        return cmcList.joinToString("")
    }

    //for filterpopup in deckfragment, returns search query for card type
    fun filterType(
        isChecked: Boolean,
        typeList: MutableList<String>,
        s: String,
        btn: ToggleButton
    ): String {
        if (isChecked) {
            if (!typeList.contains("type=$s")) {
                typeList.add("type=$s")
            }
            btn.background =
                getApplication<Application>().resources.getDrawable(R.drawable.btn_selected, null)
            btn.setTextColor(ContextCompat.getColor(getApplication<Application>(), R.color.bar))
        } else if (!isChecked) {
            if (typeList.contains("type=$s")) {
                typeList.remove("type=$s")
            }
            btn.background = getApplication<Application>().getDrawable(R.drawable.btn_unselected)
            btn.setTextColor(ContextCompat.getColor(getApplication<Application>(), R.color.rose))
        }
        return "(${typeList.joinToString("+OR+")})"
    }

}
