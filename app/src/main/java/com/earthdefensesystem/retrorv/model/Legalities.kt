package com.earthdefensesystem.retrorv.model

import com.google.gson.annotations.SerializedName

data class Legalities (

	@SerializedName("format") val format : String? = null,
	@SerializedName("legality") val legality : String? = null
)