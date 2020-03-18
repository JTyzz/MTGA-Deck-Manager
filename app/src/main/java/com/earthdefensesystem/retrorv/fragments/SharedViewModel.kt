package com.earthdefensesystem.retrorv.fragments

import android.app.Application
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.earthdefensesystem.retrorv.R
import com.earthdefensesystem.retrorv.database.AppDatabase
import com.earthdefensesystem.retrorv.database.DeckRepo
import com.earthdefensesystem.retrorv.model.*
import com.earthdefensesystem.retrorv.network.ApiFactory
import com.earthdefensesystem.retrorv.network.SearchRepo
import com.earthdefensesystem.retrorv.utilities.ImageStoreManager
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class SharedViewModel(application: Application) : AndroidViewModel(application), CoroutineScope {
    private val searchRepo: SearchRepo = SearchRepo(ApiFactory.apiService)
    private val repo: DeckRepo
    private val context = application.applicationContext

    private val parentJob = Job()
    override val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Default
    private val scope = CoroutineScope(coroutineContext)

    //search fragment
    val cardList = MutableLiveData<List<Card>>()

    //list fragment
    var deckNamesLD: LiveData<List<String>>
    var deckNames: MutableList<String> = mutableListOf()
    var allLDDecks: LiveData<List<Deck>>

    //deck fragment
    var mCurrentDeck: LiveData<DeckWithCards>? = null
    var mDeckId = MutableLiveData<Long>()


    init {
        //get reference to deckdao from appdatabase to construct deckrepo
        val deckDao = AppDatabase.getDatabase(application, viewModelScope).deckDao()
        repo = DeckRepo(deckDao)
        allLDDecks = repo.allLDDecks
        deckNamesLD = repo.deckNames
    }

    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }
    /* SEARCH FRAGMENT FUNCTIONS*/

    fun loadSearchCards(query: String) {
        scope.launch {
            val searchCards = searchRepo.getSearchCards(query)
            searchCards?.removeIf { it.imageUris?.small == null }
            cardList.postValue(searchCards)
        }
    }

    fun getCardsByDeckId(deckId: Long) {
        scope.launch { mCurrentDeck = repo.getDeckById(deckId) }
    }

    fun deleteDeckById() {
        if (mCurrentDeck?.value?.cards.isNullOrEmpty()) {
            scope.launch {
                repo.deleteDeckById(mDeckId.value!!)
            }
        }
    }

    //viewmodel specific coroutine scope for threads so insert doesnt block ui
    suspend fun insertDeck(deck: Deck) = viewModelScope.launch {
        repo.insertDeck(deck)
    }

    suspend fun insertCardtoDeck(card: Card, count: Int) = viewModelScope.launch {
        val currentDeck = mCurrentDeck?.value!!.deck
        val cardCount = CardCount(card.cardId, count, card)
        repo.insertCardCount(cardCount)
        val junction = DeckCardJoin(cardCount.id, currentDeck.deckId!!)
        repo.insertRelation(junction)
        if (currentDeck.cIdentity.isNullOrEmpty()) {
            updateDeckColorId(currentDeck.deckId!!, card.colors!!.joinToString(","))
        } else {
            val newDeckColors = checkDeckColorId(card, currentDeck)
            if (currentDeck.cIdentity != newDeckColors) {
                updateDeckColorId(currentDeck.deckId!!, newDeckColors)
            }
        }
    }

    fun updateDeckColorId(deckId: Long, colorId: String) = viewModelScope.launch {
        repo.updateDeckColorId(deckId, colorId)
    }

    fun updateCardCount(oldCard: CardCount, count: Int) = viewModelScope.launch {
        val newCardCount = CardCount(oldCard.id, count, oldCard.card)
        repo.insertCardCount(newCardCount)
    }

    fun updateDeckBackground(deck: Deck, cardCount: CardCount, imageView: ImageView) =
        viewModelScope.launch {
            Log.d("salami", "${deck.name} updating background")
            Glide.with(context)
                .asBitmap()
                .load(cardCount.card.imageUris?.artCrop)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        ImageStoreManager.saveToInternalStorage(
                            context,
                            resource,
                            cardCount.card.cardId
                        )
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                    }
                })
            repo.updateDeckBackground(deck.deckId!!, cardCount.card.cardId)
        }


    suspend fun newDeck() = viewModelScope.launch {
        val word = "New Deck"
        val time = System.currentTimeMillis()
        val deck = Deck(word, time)
        var deckId: Long = 0
        checkExistingName(deck)
        insertDeck(deck)
        async { deckId = repo.getNewDeckId(deck.name)}.await()
        async { setDeckId(deckId)}.await()
    }


    //checks name of deck and increments name by 1 if it exists
    fun checkExistingName(deck: Deck): Deck {
        var deckName = deck.name
        Log.d("salami", "initial $deckName")
        var count = 0
        fun checkNames() {
            if (deckNames.contains(deckName)) {
                count++
//                if (deckNames.value!!.contains(deckName.plus(count))) {
                if (deckNames.contains(deckName.plus(count))) {
                    Log.d("salami", count.toString())
                    checkNames()
                }
            }
        }
        checkNames()
        if (count != 0) {
            deckName = deckName.plus(count)
            deck.name = deckName
            Log.d("salami", "changed ${deck.name}")
        }
        return deck
    }

    fun drawChart(chart: BarChart, cardList: List<CardCount>) {
        getDeckId()
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

        data.barWidth = 0.4f
        chart.data = data
        chart.setDrawBarShadow(true)
        chart.invalidate()
    }

    fun setDeckId(deckId: Long) {
        this.mDeckId.value = deckId
    }

    fun getDeckId(): Long {
        return mDeckId.value!!
    }


    fun checkDeckColorId(card: Card, deck: Deck): String {
        val cardColor = card.colors
        val deckColor = deck.cIdentity?.split(",")?.toTypedArray()

        for (item in cardColor!!.iterator()) {
            if (deckColor?.contains(item)!!) {
                continue
            } else {
                deckColor.plus(item)
                Log.d("colors", "$item has been added")
            }
        }
        val sortedDeckColor = deckColor?.apply {
            sort()
        }
        return sortedDeckColor?.joinToString(",")!!
    }
}
