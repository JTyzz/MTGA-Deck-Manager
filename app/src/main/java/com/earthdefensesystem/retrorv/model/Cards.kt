package com.earthdefensesystem.retrorv.model


import androidx.room.*
import com.google.gson.annotations.SerializedName

data class Base(

    @SerializedName("data")
    val cards: List<Card>
)

data class Card(
    @SerializedName("cmc")
    val cmc: Int?,
    @SerializedName("colors")
    val colors: List<String>?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("image_uris")
    val imageUris: ImageUris?,
    @SerializedName("mana_cost")
    val manaCost: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("rarity")
    val rarity: String?,
    @SerializedName("set")
    val `set`: String?,
    @SerializedName("set_name")
    val setName: String?,
    @SerializedName("type_line")
    val typeLine: String?,
    @SerializedName("oracle_text")
    val oracleText: String?
)

@Entity(tableName = "card_table")
data class Cards(
    val cmc: Int,
    @TypeConverters(StringListConverter::class)
    val colors: List<String>,
    @PrimaryKey
    @ColumnInfo(name = "cardId", index = true)
    val id: String,
    val imageUri: String,
    val manaCost: String,
    val name: String,
    val rarity: String,
    val set: String,
    val setName: String,
    val typeLine: String,
    val oracleText: String,
    val cardCount: Int
)
//data class Cards(
//	@PrimaryKey
//	@ColumnInfo(name = "cardId", index = true)
//	@SerializedName ("id") val cardId : String,
//	@SerializedName("name") val name : String? = null,
//	@SerializedName("manaCost") val manaCost : String? = null,
//	@SerializedName("cmc") val cmc : Int? = null,
//    @TypeConverters(StringListConverter::class)
//	@SerializedName("colors") val colors : List<String>? = null,
//    @TypeConverters(StringListConverter::class)
//	@SerializedName("colorIdentity") val colorIdentity : List<String>? = null,
//	@SerializedName("type") val type : String? = null,
//    @TypeConverters(StringListConverter::class)
//	@SerializedName("supertypes") val supertypes : List<String>? = null,
//    @TypeConverters(StringListConverter::class)
//	@SerializedName("types") val types : List<String>? = null,
//    @TypeConverters(StringListConverter::class)
//	@SerializedName("subtypes") val subtypes : List<String>? = null,
//	@SerializedName("rarity") val rarity : String? = null,
//	@SerializedName("set") val set : String? = null,
//	@SerializedName("setName") val setName : String? = null,
//	@SerializedName("text") val text : String? = null,
//	@SerializedName("imageUrl") val imageUrl : String? = null,
//    @TypeConverters(StringListConverter::class)
//	@SerializedName("printings") val printings : List<String>? = null,
//	@SerializedName("originalText") val originalText : String? = null,
//	@SerializedName("originalType") val originalType : String? = null,
//	val cardCount : Int? = 0
//)

data class ImageUris(
    @SerializedName("art_crop")
    val artCrop: String?,
    @SerializedName("border_crop")
    val borderCrop: String?,
    @SerializedName("large")
    val large: String?,
    @SerializedName("normal")
    val normal: String?,
    @SerializedName("png")
    val png: String?,
    @SerializedName("small")
    val small: String?
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

class StringListConverter {
    @TypeConverter
    fun toString(stringList: List<String>): String {
        return stringList.joinToString()
    }

    @TypeConverter
    fun toStringList(string: String): List<String> {
        return string.split(",")
    }
}