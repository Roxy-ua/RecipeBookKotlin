package com.example.tapasoft.recipekotlin.api

import com.example.tapasoft.recipekotlin.Config
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by udav on 27-Jul-19.
 */

object ApiClient {

    var gson = GsonBuilder()
            .setLenient()
            .create()

    fun retrofit(): Retrofit = Retrofit.Builder()
            .client(OkHttpClient())
            .baseUrl(Config.SERVER_API_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()

    val clientApi: ApiInterface = retrofit().create(ApiInterface::class.java)
}