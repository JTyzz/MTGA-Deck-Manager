package com.earthdefensesystem.retrorv.model


import androidx.appcompat.widget.DialogTitle
import androidx.room.*
import com.google.gson.annotations.SerializedName

data class Base(

    @SerializedName("data")
    val cards: List<Card>
)

data class Card(
    @SerializedName("cmc")
    var cmc: Int?,
    @TypeConverters(StringListConverter::class)
    @SerializedName("colors")
    var colors: List<String>?,
    @SerializedName("id")
    var cardId: String,
    @SerializedName("mana_cost")
    var manaCost: String?,
    @SerializedName("name")
    var name: String?,
    @SerializedName("rarity")
    var rarity: String?,
    @SerializedName("set")
    var cardSet: String?,
    @SerializedName("set_name")
    var setRelease: String?,
    @SerializedName("type_line")
    var typeLine: String?,
    @SerializedName("oracle_text")
    var oracleText: String?,
    @Embedded
    @SerializedName("image_uris")
    var imageUris: ImageUris?
)

//class to allow decks to have different amounts of one card object
@Entity(tableName = "cc_table")
data class CardCount(
    @ColumnInfo(name = "cc_id", index = true)
    @PrimaryKey
    val id: String,
    val count: Int,
    @Embedded
    val card: Card
)


@Entity(tableName = "deck_table")
data class Deck(
    var name: String,
    var date: Long? = null,
    @ColumnInfo(name = "deck_id", index = true)
    @PrimaryKey(autoGenerate = true)
    var deckId: Long? = null,
    var uri: String? = null,
    var cIdentity:String? = null
)


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



//junction between card and deck
@Entity(primaryKeys = ["deck_id", "cc_id"])
data class DeckCardJoin(
    @ColumnInfo(name = "cc_id", index = true)
    val CCId: String,
    @ColumnInfo(name = "deck_id", index = true)
    val deckId: Long
)

//single deck multiple card class
data class DeckWithCards(
    @Embedded val deck: Deck,
    @Relation(
        parentColumn = "deck_id",
        entityColumn = "cc_id",
        associateBy = Junction(DeckCardJoin::class)
    )
    val cards: List<CardCount> = emptyList()
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