package com.earthdefensesystem.retrorv.model


import androidx.appcompat.widget.DialogTitle
import androidx.room.*
import com.google.gson.annotations.SerializedName

data class Base(

    @SerializedName("data")
    val cards: List<Card>
)

@Entity(tableName = "card_table")
data class Card(
    @SerializedName("cmc")
    var cmc: Int?,
    @TypeConverters(StringListConverter::class)
    @SerializedName("colors")
    var colors: List<String>?,
    @PrimaryKey
    @ColumnInfo(name = "cardId", index = true)
    @SerializedName("id")
    var id: String,
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
    var count: Int,
    @Ignore
    @SerializedName("image_uris")
    var imageUris: ImageUris?

) {
    constructor(
        cmc: Int?,
        colors: List<String>?,
        id: String,
        manaCost: String?,
        name: String,
        rarity: String?,
        cardSet: String?,
        setRelease: String?,
        typeLine: String?,
        oracleText: String?,
        count: Int
    ) : this(
        cmc, colors, id, manaCost, name,
        rarity, cardSet, setRelease, typeLine,
        oracleText, count, null
    )
}


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
    @ColumnInfo(index = true)
    @PrimaryKey(autoGenerate = true)
    var deckId: Long? = null,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    var deckImg: ByteArray? = null
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
    val cards: List<Card>
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