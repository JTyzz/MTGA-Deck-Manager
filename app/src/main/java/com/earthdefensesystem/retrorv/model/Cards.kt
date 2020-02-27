package com.earthdefensesystem.retrorv.model


import androidx.annotation.NonNull
import androidx.room.*
import com.google.gson.annotations.SerializedName

@Entity(tableName = "card_table")
data class Cards(
	@PrimaryKey
	@ColumnInfo(name = "cardId", index = true)
	@SerializedName ("id") val cardId : String,
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
	@SerializedName("originalType") val originalType : String? = null,
	val cardCount : Int? = 0
)

@Entity(tableName = "deck_table")
data class Deck(
	var name: String,
	var date: Long? = null,
//	var imgPath: String? = null,
	@ColumnInfo(index = true)
	@PrimaryKey(autoGenerate = true)
	var deckId: Long? = null
)
//junction between card and deck
@Entity(primaryKeys = ["deckId", "cardId"])
data class DeckCardJoin(
	@ColumnInfo(name = "cardId", index = true)
	val cardId: String,
	@ColumnInfo(name = "deckId", index = true)
	val deckId: Long
)

//single deck multiple card class
data class DecksWithCards(
	@Embedded val deck: Deck,
	@Relation(
		parentColumn = "deckId",
		entityColumn = "cardId",
		associateBy = Junction(DeckCardJoin::class)
	)
	val cards: List<Cards>
)