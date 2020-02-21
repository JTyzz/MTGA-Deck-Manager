package com.earthdefensesystem.retrorv.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "decks")
data class Deck(
    @PrimaryKey(autoGenerate = true) var id: String? = null,
    var name: String? = null,
    var date: Long? = null,
    var imgPath: String? = null

)