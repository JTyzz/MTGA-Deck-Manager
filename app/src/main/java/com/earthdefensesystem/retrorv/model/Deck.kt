package com.earthdefensesystem.retrorv.model


data class Deck(var name: String) {
    var cards = mutableMapOf<Card, Long>()


    fun addNewDeck(){

    }
}