package com.mkd.whatstheweather.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouriteDao {

    @Query("SELECT * from Favourite ORDER BY id ASC")
    fun getAllFavourites(): Flow<List<Favourite>>

    @Query("SELECT * from Favourite WHERE city LIKE :city LIMIT 1")
    fun getItemByName(city: String): Flow<Favourite>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(favourite: Favourite)

    @Delete
    suspend fun delete(favourite: Favourite)
}