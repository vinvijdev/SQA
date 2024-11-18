package com.adiuxm.genaisqa.data.remote

import android.content.Context
import com.adiuxm.genaisqa.app.DataManager
import com.adiuxm.genaisqa.domain.repository.ImageUploadResponseRepository
import com.adiuxm.genaisqa.domain.repository.QueryResponseRepository
import com.adiuxm.genaisqa.domain.repository.TokenResponseRepository
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object ApiModule {
    private fun providesGson(): Gson =
        GsonBuilder() // i have used Gson() library for json conversion you can use this by adding this library in gradle
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()

    private fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor(
            provideLoggingInterceptor()
        ).addInterceptor(AuthInterceptor)
            .connectTimeout(2, TimeUnit.MINUTES)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
//            .authenticator(AuthAuthenticator)
            .build()
    }

    private fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        return logging
    }

    fun providesApiRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(DataManager.baseUrl)
            .client(provideOkHttpClient()).addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(providesGson())).build()
    }

    private fun providesQueryResponseService(ctx: Context): RestAPI =
        providesApiRetrofit().create(RestAPI::class.java)

    fun providesQueryResponseRepository(ctx: Context): QueryResponseRepository =
        QueryResponseRepository(providesQueryResponseService(ctx))

    private fun providesTokenResponseService(ctx: Context): RestAPI =
        providesApiRetrofit().create(RestAPI::class.java)

    fun providesTokenResponseRepository(ctx: Context): TokenResponseRepository =
        TokenResponseRepository(providesTokenResponseService(ctx))

    private fun providesImageUploadResponseService(ctx: Context): RestAPI =
        providesApiRetrofit().create(RestAPI::class.java)

    fun providesImageUploadResponseRepository(ctx: Context): ImageUploadResponseRepository =
        ImageUploadResponseRepository(providesImageUploadResponseService(ctx))
}