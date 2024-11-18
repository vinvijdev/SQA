package com.adiuxm.genaisqa.data.remote

import com.adiuxm.genaisqa.app.DataManager
import kotlinx.coroutines.runBlocking
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AuthInterceptor : Interceptor {

    private var credentials: String = Credentials.basic(
        DataManager.clientId, DataManager.clientSecret
    )

    private val tokenManager = TokenManager

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val accessToken = tokenManager.getAccessToken()
        val response = chain.proceed(originalRequest)

        // Check if the response indicates that the access token is expired
//        if (response.code != 200) {
        if (accessToken == null || (tokenManager.isAccessTokenExpired())) {

            // Make the token refresh request
            val refreshedToken = runBlocking {
                val tokenResponse = callRefreshTokenAPI()
                // Update the refreshed access token and its expiration time in the session
                tokenManager.updateAccessToken(
                    tokenResponse.body()?.accessToken!!, tokenResponse.body()?.expiresIn!!
                )
                tokenResponse.body()!!.accessToken
            }

            if (refreshedToken != null) {
                // Create a new request with the updated access token
                val newRequest =
                    originalRequest.newBuilder().header("Authorization", refreshedToken).build()
                // Retry the request with the new access token
                response.close()
                return chain.proceed(newRequest)
            }
        }
// Add the access token to the request header
        val authorizedRequest =
            originalRequest.newBuilder().header("Authorization", accessToken!!).build()
        response.close()
        return chain.proceed(authorizedRequest)
    }

    private suspend fun callRefreshTokenAPI(): retrofit2.Response<TokenResponseModel> {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val okHttpClient = OkHttpClient.Builder().addInterceptor(loggingInterceptor).build()

        val retrofit =
            Retrofit.Builder().baseUrl(
                DataManager.tokenUrl,
            )
                .addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build()
        val service = retrofit.create(RestAPI::class.java)
        return service.getTokenResponse(
            credentials, DataManager.credentialType,
        )
    }
}