package com.togethersafe.app.api

import android.util.Log
import com.togethersafe.app.data.Incident
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

private interface TogetherSafeApiInterface {
    @GET("incident")
    suspend fun getIncident(): List<Incident>
}

object TogetherSafeApi {
    private const val BASE_URL = "https://stirred-eagle-witty.ngrok-free.app"
    private var retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private var api: TogetherSafeApiInterface = retrofit.create(TogetherSafeApiInterface::class.java)

    suspend fun getIncident() {
        try {
            val incidents = api.getIncident()
            Log.d("INCIDENT", incidents.toString())
        } catch (e: Exception) {
            Log.d("INCIDENT", e.stackTraceToString())
        }
    }
}