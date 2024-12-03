package com.example.rescovery

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import androidx.room.OnConflictStrategy
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RestaurantDatabaseDao {
    @Insert
    suspend fun insert(restaurant: Restaurant)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(restaurants: List<Restaurant>)

    @Update
    suspend fun updateRestaurant(restaurant: Restaurant)

    @Query("DELETE FROM restaurant_table")
    suspend fun clearTable()

    @Query("SELECT * FROM restaurant_table")
    fun getAllRestaurants(): Flow<List<Restaurant>> //for MAP

    @Query("SELECT * FROM restaurant_table WHERE id = :restaurantId")
    suspend fun getRestaurantById(restaurantId: Long): Restaurant?

    @Update
    suspend fun update(restaurant: Restaurant)

    @Delete
    suspend fun delete(restaurant: Restaurant)
}