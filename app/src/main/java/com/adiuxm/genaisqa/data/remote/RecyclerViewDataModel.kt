package com.adiuxm.genaisqa.data.remote

import java.io.File

data class RecyclerViewDataModel(
    val message: String?,
    val type: String,
    val chatType: String,
    val file: File? = null
)
