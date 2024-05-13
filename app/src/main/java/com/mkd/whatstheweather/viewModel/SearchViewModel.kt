package com.mkd.whatstheweather.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mkd.whatstheweather.api.ApiResult
import com.mkd.whatstheweather.api.RetrofitService
import com.mkd.whatstheweather.api.toResultFlow
import com.mkd.whatstheweather.model.CityResponse
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {

    private val liveDataCities: MutableLiveData<ApiResult<CityResponse?>?> =
        MutableLiveData<ApiResult<CityResponse?>?>()

    fun getCities(name: String): MutableLiveData<ApiResult<CityResponse?>?> {
        viewModelScope.launch {
            val queryParams = mapOf("q" to name)
            val flow = toResultFlow { RetrofitService.getInstance().getCity(queryParams) }
            flow.collect { result ->
                liveDataCities.value = result
            }
        }
        return liveDataCities
    }
}