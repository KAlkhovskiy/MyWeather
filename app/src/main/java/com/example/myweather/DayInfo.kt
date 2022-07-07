package com.example.myweather

data class DayInfo(
    val dayTime: String,
    val description: String,
    val tempMin: String,
    val tempMax: String,
    val img: Int,
    val city: String,
    val temp: String,
    val feelsLike: String,
    val windSpeed: String,
    val deg: String
)
