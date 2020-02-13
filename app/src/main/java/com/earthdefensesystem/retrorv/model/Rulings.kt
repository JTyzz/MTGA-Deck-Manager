package com.earthdefensesystem.retrorv.model

import com.google.gson.annotations.SerializedName

data class Rulings (

	@SerializedName("date") val date : String? = null,
	@SerializedName("text") val text : String? = null
)