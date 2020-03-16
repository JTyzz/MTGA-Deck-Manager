package com.earthdefensesystem.retrorv.fragments

import android.app.Application
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import com.earthdefensesystem.retrorv.R
import android.view.View
import androidx.lifecycle.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class SearchViewModel(application: Application) : AndroidViewModel(application) {
    private val searchRepo: SearchRepo = SearchRepo(ApiFactory.apiService)
    private val repo: DeckRepo
    private val context = application.applicationContext

    private val parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Default
    private val scope = CoroutineScope(coroutineContext)

    //search fragment
    val searchCardsLiveData = MutableLiveData<List<Card>>()

    //list fragment
    var deckNamesLD: LiveData<List<String>>
    var deckNames: MutableList<String> = mutableListOf()
    var allLDDecks: LiveData<List<Deck>>

    //deck fragment
    var openDeckCard: LiveData<DecksWithCards>? = null


    init {
        //get reference to deckdao from appdatabase to construct deckrepo
        val deckDao = AppDatabase.getDatabase(application, viewModelScope).deckDao()
        repo = DeckRepo(deckDao)
        allLDDecks = repo.allLDDecks
        deckNamesLD = repo.deckNames
    }

    fun getCardsByDeckId(deckId: Long) {
        scope.launch {
            openDeckCard = repo.getDeckById(deckId)
        }
    }
    fun deleteDeckById() {
        scope.launch {
            if (openDeckCard?.value?.cards!!.isEmpty()) {
                repo.deleteDeckById(openDeckCard?.value?.deck?.deckId!!)
            }
        }
    }

    //viewmodel specific coroutine scope for threads so insert doesnt block ui
    fun insertDeck(deck: Deck) = viewModelScope.launch {
        repo.insertDeck(deck)
    }

    fun insertCardtoDeck(card: Card, deckId: Long, count: Int) = viewModelScope.launch {
        val cardCount = CardCount(card.cardId, count, card)
        Log.d("salami", "${cardCount.card.name} inserted")
        repo.insertCardCount(cardCount)
        val junction = DeckCardJoin(cardCount.id, deckId)
        Log.d("salami", "${cardCount.card.name} inserted into $deckId")
        repo.insertRelation(junction)
    }

    fun updateCardCount(oldCard: CardCount, count: Int) = viewModelScope.launch {
        val newCardCount = CardCount(oldCard.id, count, oldCard.card)
        repo.insertCardCount(newCardCount)
    }

    fun updateDeckBackground(deck: Deck, cardCount: CardCount) = viewModelScope.launch {
        Log.d("salami", "${deck.name} updating background")
        Glide.with(context)
            .asBitmap()
            .load(cardCount.card.imageUris?.artCrop)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    ImageStoreManager.saveToInternalStorage(
                        context,
                        resource,
                        cardCount.card.cardId
                    )
                    Log.d("salami", "Picture added successfully")
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })
        repo.updateDeckBackground(deck.deckId!!, cardCount.card.cardId)

    }

    fun getCardsSearch(color: String) {
        scope.launch {
            val searchCards = searchRepo.getSearchCards(color)
            searchCards?.removeIf { it.imageUris?.small == null }
            searchCardsLiveData.postValue(searchCards)
        }
    }


    fun newDeck(){
        val word = "New Deck"
        val time = System.currentTimeMillis()
        val deck = Deck(word, time)
        checkExistingName(deck)
        insertDeck(deck)
        viewModelScope.launch {
            val deckId = repo.getNewDeckId(deck.name)
            getCardsByDeckId(deckId)
        }
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

    fun drawChart(chart: BarChart){
        val list = openDeckCard?.value?.cards!!

        val cmc1 = list.count { it.card.cmc == 1 }.toFloat()
        val cmc2 = list.count { it.card.cmc == 2 }.toFloat()
        val cmc3 = list.count { it.card.cmc == 3 }.toFloat()
        val cmc4 = list.count { it.card.cmc == 4 }.toFloat()
        val cmc5 = list.count { it.card.cmc == 5 }.toFloat()
        val cmc6 = list.count { it.card.cmc!! >= 6 }.toFloat()
        val cmcList = listOf(cmc1, cmc2, cmc3, cmc4, cmc5, cmc6)

        chart.setDrawBarShadow(false)
        chart.setDrawValueAboveBar(true)
        val description = Description()
        description.text = ""
        chart.description = description
        chart.setMaxVisibleValueCount(50)
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
        leftAxis.axisMaximum = cmcList.max()!!
        chart.axisRight.isEnabled = false

        val entries: ArrayList<BarEntry> = ArrayList()
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
        set.barShadowColor = getApplication<Application>().resources.getColor(R.color.extradark, null)
        val data = BarData(set)

        data.barWidth = 0.3f
        chart.data = data
        chart.setFitBars(true)
        chart.setDrawBarShadow(true)
        chart.invalidate()



    }

    fun <T> MutableLiveData<T>.notifyObserver() {
        this.value = this.value
    }
}
