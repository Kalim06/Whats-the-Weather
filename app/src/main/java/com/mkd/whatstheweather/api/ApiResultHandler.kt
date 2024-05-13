package com.mkd.whatstheweather.api

import androidx.lifecycle.MutableLiveData

class ApiResultHandler<T>(
    private val onSuccess: (T?) -> Unit,
    private val onFailure: (ApiError<T?>) -> Unit
) {
    val loading = MutableLiveData<Boolean>()

    fun handleApiResult(result: ApiResult<T?>) {
        loading.postValue(result.status == ApiStatus.LOADING)
        when (result.status) {
            ApiStatus.SUCCESS -> {
                loading.postValue(false)
                onSuccess(result.data)
            }

            ApiStatus.ERROR -> {
                loading.postValue(false)
                onFailure(ApiError(result.status, result.data, result.message))
            }
        }
    }
}