package com.example.rescovery

import UserInput
import android.icu.text.UnicodeSet
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RestaurantViewModel(private val restaurantRepository: RestaurantRepository, private val userInputRepository: UserInputRepository) : ViewModel() {
    val allRestaurantLive: LiveData<List<Restaurant>> = restaurantRepository.allRestaurant.asLiveData()

    fun getAllUserInputsForRestaurant(restaurantId: Long): LiveData<List<UserInputWithRestaurant>> { //for displaying all posts for a certain restaurant
        return userInputRepository.getAllUserInputsForRestaurant(restaurantId).asLiveData()
    }

    fun getAllUserInputs(): LiveData<List<UserInputWithRestaurant>> { //for displaying all user inputs for feed
        return userInputRepository.getAllUserInputs().asLiveData()
    }

    fun getUserInputs(userId: Long): LiveData<List<UserInputWithRestaurant>> { //for displaying all inputs for a specific user for profile
        return userInputRepository.getUserInputs(userId).asLiveData()
    }

    fun getAllRestaurants(): LiveData<List<Restaurant>> { //for getting all restaurants for MAP
        return restaurantRepository.getAllRestaurants().asLiveData()
    }

    fun getRestaurantById(restaurantId: Long) : Restaurant? {
        return restaurantRepository.getRestaurantById(restaurantId)
    }

    fun insertRestaurant(restaurant: Restaurant) {
        viewModelScope.launch(Dispatchers.IO)  {
            restaurantRepository.insert(restaurant)
        }
    }

    fun deleteRestaurant(restaurant: Restaurant) { //probably wont use this
        viewModelScope.launch(Dispatchers.IO)  {
            restaurantRepository.delete(restaurant)
        }
    }

    fun insertUserInput(userInput: UserInput) {
        viewModelScope.launch(Dispatchers.IO) {
            userInputRepository.insert(userInput)
        }
    }

    fun deleteUserInput(userInput: UserInput) {
        viewModelScope.launch(Dispatchers.IO) {
            userInputRepository.delete(userInput)
        }
    }
}

class RestaurantFactory (private val restaurantRepository: RestaurantRepository, private val userInputRepository: UserInputRepository) : ViewModelProvider.Factory {
    override fun< T: ViewModel > create(modelClass: Class<T>) : T {
        if(modelClass.isAssignableFrom(RestaurantViewModel::class.java))
            return RestaurantViewModel(restaurantRepository, userInputRepository) as T
        throw IllegalArgumentException("Error")
    }
}