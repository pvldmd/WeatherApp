package com.pvld.weatherapp.network.entities

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherApi {

    @GET("/data/2.5/weather?units=metric&appid=7cbe829e6599df42cb5a8b9e0a8779e0")
    suspend fun loadWeatherData(@Query("q") city: String): Response<WeatherData>
}