package com.mkd.whatstheweather.viewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mkd.whatstheweather.room.FavouriteDao
import com.mkd.whatstheweather.viewModel.FavouriteViewModel

class FavouriteViewModelFactory(private val favouriteDao: FavouriteDao) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavouriteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return FavouriteViewModel(favouriteDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}