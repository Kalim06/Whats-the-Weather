package com.mkd.whatstheweather.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun convertTimeStamp(timestamp: Long): String {
    val milliseconds = timestamp * 1000
    val date = Date(milliseconds)
    val formatter = SimpleDateFormat("hh:mm a dd-MM-yyyy", Locale.getDefault())
    return formatter.format(date)
}

fun getFormattedTime(timestamp: Long): String {
    val milliseconds = timestamp * 1000
    val date = Date(milliseconds)
    val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return formatter.format(date)
}

fun formatTemperatureMetric(kelvin: Double): String {
    val celsius = (kelvin - 273.15).toInt()
    return "$celsius °C"
}

fun formatTemperatureImperial(kelvin: Double): String {
    val fahrenheit = (kelvin * 9 / 5 - 459.67).toInt()
    return "$fahrenheit °F"
}

fun formatWindSpeedMetric(metersPerSecond: Double): String {
    return "${metersPerSecond.toInt()} m/s"
}

fun formatWindSpeedImperial(metersPerSecond: Double): String {
    val milesPerHour = metersPerSecond * 2.23694 // Convert m/s to mph
    return "${milesPerHour.toInt()} mph"
}

fun showKeyboard(view: View) {
    val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
}

fun hideKeyboard(view: View) {
    val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun showSnackbar(view: View, message: String) {
    Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
}

fun showSnackbarWithDismiss(
    view: View,
    message: String
) {
    Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE)
        .setAction("Dismiss") {
            //Get preferences
            val preferencesManager = PreferencesManager(view.context)
            preferencesManager.setHintShown(true)
        }
        .show()
}

