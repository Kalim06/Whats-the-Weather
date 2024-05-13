package com.mkd.whatstheweather.model

data class WeatherResponse(
    val visibility: Int?,
    val timezone: Int?,
    val main: Main?,
    val clouds: Clouds?,
    val sys: Sys?,
    val dt: Int?,
    val coord: Coord?,
    val weather: List<WeatherItem>?,
    val name: String?,
    val cod: Int?,
    val id: Int?,
    val base: String?,
    val wind: Wind?
)

data class Main(
    val temp: Double?,
    val temp_min: Double?,
    val humidity: Int?,
    val pressure: Int?,
    val feels_like: Double?,
    val temp_max: Double?
)

data class Clouds(
    val all: Int?
)

data class Sys(
    val country: String?,
    val sunrise: Int?,
    val sunset: Int?,
    val id: Int?,
    val type: Int?
)

data class Coord(
    val lon: Double?,
    val lat: Double?
)

data class WeatherItem(
    val icon: String?,
    val description: String?,
    val main: String?,
    val id: Int?
)

data class Wind(
    val deg: Int?,
    val speed: Double?
)

data class WeatherDetail(
    val time: String,
    val name: String,
    val icon: String,
    val description: String,
    val temp: String,
    val feelsLike: String,
    val humidity: String,
    val wind: String,
    val sunrise: String,
    val sunset: String
)