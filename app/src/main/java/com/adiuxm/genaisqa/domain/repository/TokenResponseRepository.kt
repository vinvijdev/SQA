package com.adiuxm.genaisqa.domain.repository

import com.adiuxm.genaisqa.app.DataManager
import com.adiuxm.genaisqa.data.remote.Resource
import com.adiuxm.genaisqa.data.remote.RestAPI
import com.adiuxm.genaisqa.data.remote.TokenResponseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.Credentials
import retrofit2.Response

class TokenResponseRepository(private val restAPI: RestAPI) {

    suspend fun getResult() = flow {
        emit(Resource.Loading())

        val credentials: String =
            Credentials.basic(
                DataManager.clientId,
                DataManager.clientSecret
            )

        restAPI.getTokenResponse(
            credentials, DataManager.credentialType
        ).let { response ->
            val resource = handleResponse(response)
            emit(resource)
        }
    }.flowOn(Dispatchers.IO)

    private fun handleResponse(response: Response<TokenResponseModel>): Resource<TokenResponseModel> {
        if (response.isSuccessful) {
            return Resource.Success(response.body())
        }
        return Resource.Error(response.message(), response.body())
    }
}
