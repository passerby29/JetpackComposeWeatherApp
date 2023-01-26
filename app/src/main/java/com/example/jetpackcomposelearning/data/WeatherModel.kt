package com.example.jetpackcomposelearning.data

data class WeatherModel(
    var city: String,
    val time: String,
    var currentTemp: String,
    val condition: String,
    val icon: String,
    val maxTemp: String,
    val minTemp: String,
    val avgTemp: String,
    val hours: String
)