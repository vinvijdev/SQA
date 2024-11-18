package com.adiuxm.genaisqa.data.remote

import com.google.gson.annotations.SerializedName

data class ImageUploadResponseModel(@SerializedName("Message") val message: String, @SerializedName("response") val response: String)
