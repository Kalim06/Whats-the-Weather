package com.mkd.whatstheweather.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

fun <T> toResultFlow(call: suspend () -> Response<T>?): Flow<ApiResult<T>?> {
    return flow {
        emit(ApiResult.Loading(null))
        try {
            call()?.let { response ->
                if (response.isSuccessful && response.body() != null) {
                    emit(ApiResult.Success(response.body()))
                } else {
                    response.errorBody()?.let {
                        emit(ApiResult.Error(it.string()))
                    }
                }
            }
        } catch (e: Exception) {
            emit(ApiResult.Error(e.toString()))
        }
    }.flowOn(Dispatchers.IO)
}