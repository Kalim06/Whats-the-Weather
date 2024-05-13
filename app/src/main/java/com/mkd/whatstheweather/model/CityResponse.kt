package com.mkd.whatstheweather.model

data class CityResponse(
    val count: Int,
    val cod: String?,
    val message: String?,
    val list: List<City>?
)

data class City(
    val id: Int?,
    val name: String?,
    val coord: Coord?,
    val sys: Sys?
)

