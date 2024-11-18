package com.adiuxm.genaisqa.data.remote

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part


interface RestAPI {
    @POST("/genAiMobile/query")
    @Headers("User-Agent: PostmanRuntime/7.42.0")
    suspend fun getQueryResponse(
        @Body body: QueryRequestModel,
        @Header("Accept") accept: String
    ): Response<QueryResponseModel>

    @POST("/getLLMResponse")
    suspend fun getQueryResponse(@Body body: QueryRequestModel): Response<QueryResponseModel>

    @POST("/oauth/token")
    @FormUrlEncoded
    suspend fun getTokenResponse(
        @Header("authorization") authorization: String,
        @Field("grant_type") grantType: String,
    ): Response<TokenResponseModel>

    @Multipart
    @POST("/upload")
    suspend fun uploadFile(@Part image: MultipartBody.Part): Response<ImageUploadResponseModel>
}