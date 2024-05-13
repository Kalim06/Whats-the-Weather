package com.mkd.whatstheweather

import android.app.Application
import com.mkd.whatstheweather.room.FavouriteDatabase

class MyApplication : Application() {
    val database: FavouriteDatabase by lazy { FavouriteDatabase.getDatabase(this) }
}