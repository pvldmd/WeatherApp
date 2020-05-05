package com.pvld.weatherapp.network.entities

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkService {
    private const val BASE_URL = "https://api.openweathermap.org"
    private var mRetrofit: Retrofit

    init {
        mRetrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun createNetworkService(): OpenWeatherApi {
        return mRetrofit.create<OpenWeatherApi>(OpenWeatherApi::class.java)
    }
}