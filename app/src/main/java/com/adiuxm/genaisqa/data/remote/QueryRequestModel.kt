package com.adiuxm.genaisqa.data.remote

import com.google.gson.annotations.SerializedName

data class QueryRequestModel(@SerializedName("Question") val question: String, @SerializedName("AuthCode") val authCode: String)
