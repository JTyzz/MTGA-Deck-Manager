package com.earthdefensesystem.retrorv.model

import com.google.gson.annotations.SerializedName

data class ForeignNames (

	@SerializedName("name") val name : String? = null,
	@SerializedName("text") val text : String? = null,
	@SerializedName("flavor") val flavor : String? = null,
	@SerializedName("imageUrl") val imageUrl : String? = null,
	@SerializedName("language") val language : String? = null,
	@SerializedName("multiverseid") val multiverseid : Int? = null
)