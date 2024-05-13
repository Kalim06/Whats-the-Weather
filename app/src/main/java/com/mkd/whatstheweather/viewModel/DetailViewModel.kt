package com.mkd.whatstheweather.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mkd.whatstheweather.api.ApiResult
import com.mkd.whatstheweather.api.RetrofitService
import com.mkd.whatstheweather.api.toResultFlow
import com.mkd.whatstheweather.model.WeatherResponse
import kotlinx.coroutines.launch

class DetailViewModel : ViewModel() {

    private val liveDataCityWeather: MutableLiveData<ApiResult<WeatherResponse?>?> =
        MutableLiveData<ApiResult<WeatherResponse?>?>()

    fun getWeatherByCityName(cityName: String): MutableLiveData<ApiResult<WeatherResponse?>?> {
        viewModelScope.launch {
            val queryParams = mapOf("q" to cityName)
            val flow = toResultFlow { RetrofitService.getInstance().getWeatherByCityName(queryParams) }
            flow.collect { result ->
                liveDataCityWeather.value = result
            }
        }
        return liveDataCityWeather
    }
}



//class DetailViewModel : ViewModel() {
//
//    private var liveDataCityWeather: MutableLiveData<ApiResult<WeatherResponse?>?> =
//        MutableLiveData<ApiResult<WeatherResponse?>?>()
//
//    fun getWeatherByCityName(name: String): MutableLiveData<ApiResult<WeatherResponse?>?> {
//        viewModelScope.launch {
//            val flow = toResultFlow { RetrofitService.getInstance().getWeatherByCityName(name) }
//            flow.collect { result ->
//                liveDataCityWeather.value = result
//            }
//        }
//        return liveDataCityWeather
//    }
//}