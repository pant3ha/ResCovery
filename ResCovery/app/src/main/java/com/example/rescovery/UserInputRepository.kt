package com.example.rescovery

import UserInput
import androidx.room.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class UserInputRepository (private val userInputDao: UserInputDao, private val restaurantDatabaseDao: RestaurantDatabaseDao){

    fun getAllUserInputs(): Flow<List<UserInputWithRestaurant>> {
        return userInputDao.getAllUserInputs()
    }


    fun getAllUserInputsForRestaurant(restaurantId: Long): Flow<List<UserInputWithRestaurant>> {
        return userInputDao.getAllUserInputsForRestaurant(restaurantId)
    }

    fun getUserInputs(userId: Long): Flow<List<UserInputWithRestaurant>> {
        return userInputDao.getUserInputs(userId)
    }

    fun insert(userInput: UserInput) {
        CoroutineScope(Dispatchers.IO).launch {
            userInputDao.insert(userInput)
            updateOverallRating(userInput.restaurantId) //update based on new post
        }
    }

    fun delete(userInput: UserInput) {
        CoroutineScope(Dispatchers.IO).launch {
            userInputDao.delete(userInput)
            updateOverallRating(userInput.restaurantId) //update based on deleted post
        }
    }

    suspend fun updateOverallRating(restaurantId: Long) {
        //calculate average rating for each restaurant
        val userInputs = userInputDao.getAllUserInputsForRestaurant(restaurantId).first()
        val averageRating = if(userInputs.isNotEmpty()) {
            userInputs.map {it.userInput.rating}.average()
        } else {
            0.0
        }

        val restaurant = restaurantDatabaseDao.getRestaurantById(restaurantId)
        if (restaurant != null) {
            restaurant.overallRating = averageRating
            restaurantDatabaseDao.update(restaurant)
        }
    }
}