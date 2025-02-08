package com.example.star.model.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ElapsedTimeAPI {

    @POST("elapsed-time")
    suspend fun getElapsedTime(@Body timestamps: Timestamps): Response<ElapsedTimeModel>

}

data class Timestamps(
    val timestamp1 : Long,
    val timestamp2 : Long
)