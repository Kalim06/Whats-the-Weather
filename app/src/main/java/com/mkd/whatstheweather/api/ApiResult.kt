package com.mkd.whatstheweather.api

object ApiStatus {
    const val SUCCESS = "SUCCESS"
    const val ERROR = "ERROR"
    const val LOADING = "LOADING"
}

data class ApiError<out T>(
    val status: String = ApiStatus.ERROR,
    val data: T? = null,
    val message: String? = null
)

sealed class ApiResult<out T>(
    val status: String,
    val data: T?,
    val message: String?
) {
    data class Success<out R>(val resultData: R?) : ApiResult<R>(
        status = ApiStatus.SUCCESS,
        data = resultData,
        message = null
    )

    data class Error(val exception: String) : ApiResult<Nothing>(
        status = ApiStatus.ERROR,
        data = null,
        message = exception
    )

    data class Loading<out R>(val resultData: R?) : ApiResult<R>(
        status = ApiStatus.LOADING,
        data = resultData,
        message = null
    )
}