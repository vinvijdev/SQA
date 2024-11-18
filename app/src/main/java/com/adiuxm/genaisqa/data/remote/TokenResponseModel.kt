package com.adiuxm.genaisqa.data.remote

import com.google.gson.annotations.SerializedName

data class TokenResponseModel(
	@SerializedName("access_token") val accessToken: String? = null,
	val scope: String? = null,
	@SerializedName("token_type") val tokenType: String? = null,
	@SerializedName("expires_in") val expiresIn: Long? = null,
	val jti: String? = null
)

