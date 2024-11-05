package com.example.rescovery

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class RestaurantRepository(private val restaurantDatabaseDao: RestaurantDatabaseDao) {
    val allRestaurant: Flow<List<Restaurant>> = restaurantDatabaseDao.getAllRestaurants()

    fun insert(restaurant: Restaurant) {
        CoroutineScope(IO).launch {
            restaurantDatabaseDao.insert(restaurant)
        }
    }

    fun getAllRestaurants(): Flow<List<Restaurant>> { //for displaying all restaurants in MAP
        return restaurantDatabaseDao.getAllRestaurants()
    }

    fun getRestaurantById(restaurantId: Long): Flow<List<Restaurant>> {
        return restaurantDatabaseDao.getRestaurantById(restaurantId)
    }

    fun delete(restaurant: Restaurant) {
        CoroutineScope(IO).launch {
            restaurantDatabaseDao.delete(restaurant)
        }
    }
}