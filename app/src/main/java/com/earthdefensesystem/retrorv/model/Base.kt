package com.earthdefensesystem.retrorv.model

import com.google.gson.annotations.SerializedName


data class Base (
	@SerializedName("object") val listtype: String,
	@SerializedName("total_cards") val total_cards: Long,
	@SerializedName("has_more") val has_more: Boolean,
	@SerializedName("next_page") val next_page: String,
	@SerializedName("data") val cards : List<Card>
)
/*
"object": "list",
  "total_cards": 267162,
  "has_more": true,
  "next_page": "https://api.scryfall.com/cards?page=2",
 */
