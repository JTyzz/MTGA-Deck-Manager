package com.earthdefensesystem.retrorv.model

import com.google.gson.annotations.SerializedName

/*
Copyright (c) 2020 Kotlin Data Classes Generated from JSON powered by http://www.json2kotlin.com

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

For support, please feel free to contact me at https://www.linkedin.com/in/syedabsar */


data class Card (

	@SerializedName("object") val cardtype : String?,
	@SerializedName("id") val id : String,
	@SerializedName("oracle_id") val oracle_id : String,
	@SerializedName("multiverse_ids") val multiverse_ids : List<Int>,
	@SerializedName("name") val name : String,
	@SerializedName("lang") val lang : String,
	@SerializedName("released_at") val released_at : String,
	@SerializedName("uri") val uri : String,
	@SerializedName("scryfall_uri") val scryfall_uri : String,
	@SerializedName("layout") val layout : String,
	@SerializedName("highres_image") val highres_image : Boolean,
	@SerializedName("image_uris") val image_uris : ImageURIs,
	@SerializedName("mana_cost") val mana_cost : String,
	@SerializedName("cmc") val cmc : Int,
	@SerializedName("type_line") val type_line : String,
	@SerializedName("colors") val colors : List<String>,
	@SerializedName("color_identity") val color_identity : List<String>,
	@SerializedName("card_faces") val card_faces : List<CardFaces>,
	@SerializedName("legalities") val legalities : Legalities,
	@SerializedName("games") val games : List<String>,
	@SerializedName("reserved") val reserved : Boolean,
	@SerializedName("foil") val foil : Boolean,
	@SerializedName("nonfoil") val nonfoil : Boolean,
	@SerializedName("oversized") val oversized : Boolean,
	@SerializedName("promo") val promo : Boolean,
	@SerializedName("reprint") val reprint : Boolean,
	@SerializedName("variation") val variation : Boolean,
	@SerializedName("set") val set : String,
	@SerializedName("set_name") val set_name : String,
	@SerializedName("set_type") val set_type : String,
	@SerializedName("set_uri") val set_uri : String,
	@SerializedName("set_search_uri") val set_search_uri : String,
	@SerializedName("scryfall_set_uri") val scryfall_set_uri : String,
	@SerializedName("rulings_uri") val rulings_uri : String,
	@SerializedName("prints_search_uri") val prints_search_uri : String,
	@SerializedName("collector_number") val collector_number : Int,
	@SerializedName("digital") val digital : Boolean,
	@SerializedName("rarity") val rarity : String,
	@SerializedName("card_back_id") val card_back_id : String,
	@SerializedName("artist") val artist : String,
	@SerializedName("artist_ids") val artist_ids : List<String>,
	@SerializedName("illustration_id") val illustration_id : String,
	@SerializedName("border_color") val border_color : String,
	@SerializedName("frame") val frame : Int,
	@SerializedName("full_art") val full_art : Boolean,
	@SerializedName("textless") val textless : Boolean,
	@SerializedName("booster") val booster : Boolean,
	@SerializedName("story_spotlight") val story_spotlight : Boolean,
	@SerializedName("edhrec_rank") val edhrec_rank : Int,
	@SerializedName("prices") val prices : Prices,
	@SerializedName("related_uris") val related_uris : RelatedURIs,
	@SerializedName("purchase_uris") val purchase_uris : PurchaseURIs
)