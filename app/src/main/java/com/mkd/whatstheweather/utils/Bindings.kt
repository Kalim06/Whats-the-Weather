package com.mkd.whatstheweather.utils

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.mkd.whatstheweather.R
import com.mkd.whatstheweather.model.WeatherDetail


@BindingAdapter("time")
fun TextView.setTime(weather: WeatherDetail?) {
    weather?.let {
        text = weather.time
    }
}

@BindingAdapter("name")
fun TextView.setName(weather: WeatherDetail?) {
    weather?.let {
        text = weather.name
    }
}

@BindingAdapter("weatherIcon")
fun ImageView.setIcon(weather: WeatherDetail?) {
    weather?.let {
        Glide.with(this.context).load(weather.icon).into(this)
    }
}

@BindingAdapter("description")
fun TextView.setDescription(weather: WeatherDetail?) {
    weather?.let {
        text = weather.description
    }
}

@BindingAdapter("temp")
fun TextView.setTemp(weather: WeatherDetail?) {
    weather?.let {
        text = this.context.getString(R.string.temperature, weather.temp)
    }
}

@BindingAdapter("feelsLike")
fun TextView.setFeelsLike(weather: WeatherDetail?) {
    weather?.let {
        text = this.context.getString(R.string.feels_like, weather.feelsLike)
    }
}

@BindingAdapter("humidity")
fun TextView.setHumidity(weather: WeatherDetail?) {
    weather?.let {
        text = this.context.getString(R.string.humidity, weather.humidity)
    }
}

@BindingAdapter("wind")
fun TextView.setWind(weather: WeatherDetail?) {
    weather?.let {
        text = this.context.getString(R.string.wind, weather.wind)
    }
}

@BindingAdapter("sunrise")
fun TextView.setSunrise(weather: WeatherDetail?) {
    weather?.let {
        text = this.context.getString(R.string.sunrise, weather.sunrise)
    }
}

@BindingAdapter("sunset")
fun TextView.setSunset(weather: WeatherDetail?) {
    weather?.let {
        text = this.context.getString(R.string.sunset, weather.sunset)
    }
}