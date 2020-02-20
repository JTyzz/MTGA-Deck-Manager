package com.earthdefensesystem.retrorv.model

import com.google.gson.annotations.SerializedName


data class Base (

	@SerializedName("cards") val cards : List<Cards>
)

