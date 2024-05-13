package com.mkd.whatstheweather.api

import com.mkd.whatstheweather.BuildConfig
import com.mkd.whatstheweather.model.CityResponse
import com.mkd.whatstheweather.model.WeatherResponse
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.QueryMap

private const val openWeatherUrl = "http://api.openweathermap.org/data/2.5/"

interface RetrofitService {

    @GET("find")
    suspend fun getCity(
        @QueryMap options: Map<String, String>
    ): Response<CityResponse>

    @GET("weather")
    suspend fun getWeatherByCityName(
        @QueryMap options: Map<String, String>
    ): Response<WeatherResponse>

    @GET("weather")
    suspend fun getWeatherByLocation(
        @QueryMap options: Map<String, String>
    ): Response<WeatherResponse>

    companion object {
        private val retrofitService: RetrofitService by lazy {
            val interceptor = Interceptor { chain ->
                val url = chain.request()
                    .url
                    .newBuilder()
                    .addQueryParameter("appid", BuildConfig.OPEN_WEATHER_KEY)
                    .build()
                val request = chain.request()
                    .newBuilder()
                    .url(url)
                    .build()
                chain.proceed(request)
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(openWeatherUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            retrofit.create(RetrofitService::class.java)
        }

        fun getInstance(): RetrofitService = retrofitService
    }
}