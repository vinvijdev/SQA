//package com.adiuxm.genaisqa.data.remote
//
//import kotlinx.coroutines.runBlocking
//import okhttp3.Authenticator
//import okhttp3.Credentials
//import okhttp3.OkHttpClient
//import okhttp3.Request
//import okhttp3.Response
//import okhttp3.Route
//import okhttp3.logging.HttpLoggingInterceptor
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//
//object AuthAuthenticator : Authenticator {
//    override fun authenticate(route: Route?, response: Response): Request? {
////        val token = runBlocking {
////            tokenManager.getToken().first()
////        }
//        if (response.code == 401) {
//            return runBlocking {
//                val newToken = getNewToken()
////
////                if (!newToken.isSuccessful || newToken.body() == null) { //Couldn't refresh the token, so restart the login process
////                    tokenManager.deleteToken()
////                }
//
//                newToken.body()?.accessToken?.let {
////                    tokenManager.saveToken(it.token)
//                    response.request.newBuilder()
//                        .header("Authorization", "Bearer $it").build()
//                }
//            }
//        }
//        return null
//    }
//
//    private suspend fun getNewToken(): retrofit2.Response<TokenResponseModel> {
//        val loggingInterceptor = HttpLoggingInterceptor()
//        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
//        val okHttpClient = OkHttpClient.Builder().addInterceptor(loggingInterceptor).build()
//
//        val retrofit = Retrofit.Builder().baseUrl(ApiConstants.TOKEN_URL)
//            .addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build()
//        val service = retrofit.create(RestAPI::class.java)
//        return null
////        return service.getTokenResponse(
////            ApiConstants.CREDENTIAL_TYPE,
////            Credentials.basic(ApiConstants.CLIENT_ID, ApiConstants.CLIENT_SECRET)
////        )
//    }
//}