package com.example.star.model.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val BASE_URL = "https://simonepascu.pythonanywhere.com/"

    private fun getInstance() : Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val elapsedTimeAPI : ElapsedTimeAPI = getInstance().create(ElapsedTimeAPI::class.java)

}