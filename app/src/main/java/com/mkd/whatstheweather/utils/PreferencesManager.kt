package com.mkd.whatstheweather.utils

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {

    private var preferences: SharedPreferences =
        context.getSharedPreferences(PREFERENCES_NAME, privateMode)
    private var editor: SharedPreferences.Editor = preferences.edit()


    companion object {
        private var privateMode: Int = 0

        // Shared preferences file name
        const val PREFERENCES_NAME: String = "com.mkd.whatstheweather.preferences"

        // Shared preferences keys
        const val UNIT: String = "unit"
        const val HINT_SHOWN: String = "hint_shown"
    }

    fun setUnit(units: String) {
        editor.putString(UNIT, units)
        editor.apply()
    }

    fun getUnit(): String? {
        return preferences.getString(UNIT, "metric")
    }

    fun setHintShown(hintShown: Boolean) {
        editor.putBoolean(HINT_SHOWN, hintShown)
        editor.apply()
    }

    fun getHintShown(): Boolean {
        return preferences.getBoolean(HINT_SHOWN, false)
    }

}