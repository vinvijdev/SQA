package com.adiuxm.genaisqa.domain.repository

import com.adiuxm.genaisqa.data.remote.ImageUploadResponseModel
import com.adiuxm.genaisqa.data.remote.Resource
import com.adiuxm.genaisqa.data.remote.RestAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import java.io.File

class ImageUploadResponseRepository(private val restAPI: RestAPI) {

    suspend fun getResult(file: File) = flow {
        emit(Resource.Loading())
        restAPI.uploadFile(
            MultipartBody.Part
                .createFormData(
                    "image",
                    file.name,
                    file.asRequestBody()
                )
        )
            .let { response ->
                val resource = handleResponse(response)
                emit(resource)
            }
    }.flowOn(Dispatchers.IO)

    private fun handleResponse(response: Response<ImageUploadResponseModel>): Resource<ImageUploadResponseModel> {
        if (response.isSuccessful) {
            return Resource.Success(response.body())
        }
        return Resource.Error(response.message(), response.body())
    }
}
