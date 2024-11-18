package com.adiuxm.genaisqa.domain.repository

import com.adiuxm.genaisqa.app.DataManager
import com.adiuxm.genaisqa.data.remote.QueryRequestModel
import com.adiuxm.genaisqa.data.remote.QueryResponseModel
import com.adiuxm.genaisqa.data.remote.Resource
import com.adiuxm.genaisqa.data.remote.RestAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

class QueryResponseRepository(private val restAPI: RestAPI) {

    suspend fun getResult(query: String) = flow {
        emit(Resource.Loading())
        restAPI.getQueryResponse(QueryRequestModel(query, DataManager.authCode))
            .let { response ->
                val resource = handleResponse(response)
                emit(resource)
            }
    }.flowOn(Dispatchers.IO)

    private fun handleResponse(response: Response<QueryResponseModel>): Resource<QueryResponseModel> {
        if (response.isSuccessful) {
            return Resource.Success(response.body())
        }
        return Resource.Error(response.message(), response.body())
    }
}
