package com.mkd.whatstheweather.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mkd.whatstheweather.api.ApiResult
import com.mkd.whatstheweather.api.RetrofitService
import com.mkd.whatstheweather.api.toResultFlow
import com.mkd.whatstheweather.model.WeatherResponse
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val liveDataLocationWeather: MutableLiveData<ApiResult<WeatherResponse?>?> =
        MutableLiveData<ApiResult<WeatherResponse?>?>()

    fun getWeatherByLocation(
        lat: String,
        lon: String
    ): MutableLiveData<ApiResult<WeatherResponse?>?> {
        viewModelScope.launch {
            val queryParams = mapOf("lat" to lat, "lon" to lon)
            val flow = toResultFlow { RetrofitService.getInstance().getWeatherByLocation(queryParams) }
            flow.collect { result ->
                liveDataLocationWeather.value = result
            }
        }
        return liveDataLocationWeather
    }
}


//class HomeViewModel : ViewModel() {
//
//    private var liveDataLocationWeather: MutableLiveData<ApiResult<WeatherResponse?>?> =
//        MutableLiveData<ApiResult<WeatherResponse?>?>()
//
//    fun getWeatherByLocation(
//        lat: String,
//        lon: String
//    ): MutableLiveData<ApiResult<WeatherResponse?>?> {
//        viewModelScope.launch {
//            val flow = toResultFlow { RetrofitService.getInstance().getWeatherByLocation(lat, lon) }
//            flow.collect { result ->
//                liveDataLocationWeather.value = result
//            }
//        }
//        return liveDataLocationWeather
//    }
//}