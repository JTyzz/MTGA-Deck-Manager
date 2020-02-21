package com.earthdefensesystem.retrorv.model

import androidx.room.*
import com.google.gson.annotations.SerializedName

@Entity
data class Cards(
	@PrimaryKey @SerializedName ("id") val cardId : String? = null,
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
	@SerializedName("imageUrl") val imageUrl : String? = null,
	@SerializedName("printings") val printings : List<String>? = null,
	@SerializedName("originalText") val originalText : String? = null,
	@SerializedName("originalType") val originalType : String? = null
)

@Entity
data class Deck(
	var name: String,
	@PrimaryKey(autoGenerate = true) var deckId: Long? = null,
	var date: Long? = null,
	var imgPath: String? = null
)
//junction between card and deck
@Entity(primaryKeys = ["deckId", "cardId"])
data class DeckCardJoin(
	val cardId: String,
	val deckId: Long
)

//single deck multiple card class
data class DeckWithCards(
	@Embedded val deck: Deck,
	@Relation(
		parentColumn = "deckId",
		entityColumn = "songId",
		associateBy = Junction(DeckCardJoin::class)
	)
	val cards: List<Cards>
)

