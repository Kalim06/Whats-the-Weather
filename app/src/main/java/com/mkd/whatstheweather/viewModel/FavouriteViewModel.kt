package com.mkd.whatstheweather.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mkd.whatstheweather.room.Favourite
import com.mkd.whatstheweather.room.FavouriteDao
import kotlinx.coroutines.launch

class FavouriteViewModel(private val favouriteDao: FavouriteDao) : ViewModel() {

    val favouriteCities: LiveData<List<Favourite>> = favouriteDao.getAllFavourites().asLiveData()

    fun addCity(city: String) {
        val favourite = Favourite(city = city)
        insertItem(favourite)
    }

    private fun insertItem(favourite: Favourite) {
        viewModelScope.launch {
            favouriteDao.insert(favourite)
        }
    }

    fun retrieveFavouriteByName(name: String): LiveData<Favourite> {
        return favouriteDao.getItemByName(name).asLiveData()
    }

    fun deleteFavourite(favourite: Favourite) {
        viewModelScope.launch {
            favouriteDao.delete(favourite)
        }
    }
}