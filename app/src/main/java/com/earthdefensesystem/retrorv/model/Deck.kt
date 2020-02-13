package com.earthdefensesystem.retrorv.model

data class Deck(
    var name: String? = null
){

    public fun checkList(deckList: ArrayList<Deck>) {
        var count = 0
        if (deckList.contains(Deck("New Deck$count"))) {
            count++
            checkList(deckList)
        }
    }
}