package com.earthdefensesystem.retrorv.model

import com.google.gson.annotations.SerializedName

data class Cards (

	@SerializedName("name") val name : String? = null,
	@SerializedName("manaCost") val manaCost : String? = null,
	@SerializedName("cmc") val cmc : Int? = null,
	@SerializedName("colors") val colors : List<String>? = null,
	@SerializedName("colorIdentity") val colorIdentity : List<String>? = null,
	@SerializedName("type") val type : String? = null,
	@SerializedName("supertypes") val supertypes : List<String>? = null,
	@SerializedName("types") val types : List<String>? = null,
	@SerializedName("subtypes") val subtypes : List<String>? = null,
	@SerializedName("rarity") val rarity : String? = null,
	@SerializedName("set") val set : String? = null,
	@SerializedName("setName") val setName : String? = null,
	@SerializedName("text") val text : String? = null,
	@SerializedName("artist") val artist : String? = null,
	@SerializedName("number") val number : String? = null,
	@SerializedName("layout") val layout : String? = null,
	@SerializedName("multiverseid") val multiverseid : Int? = null,
	@SerializedName("imageUrl") val imageUrl : String? = null,
	@SerializedName("rulings") val rulings : List<Rulings>? = null,
	@SerializedName("foreignNames") val foreignNames : List<ForeignNames>? = null,
	@SerializedName("printings") val printings : List<String>? = null,
	@SerializedName("originalText") val originalText : String? = null,
	@SerializedName("originalType") val originalType : String? = null,
	@SerializedName("legalities") val legalities : List<Legalities>? = null,
	@SerializedName("id") val id : String? = null
)