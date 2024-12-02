package com.example.rescovery

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OldRestaurantViewModel(private val restaurantRepository: RestaurantRepository, private val userInputRepository: UserInputRepository) : ViewModel() {
    val allRestaurantLive: LiveData<List<Restaurant>> = restaurantRepository.allRestaurant.asLiveData()

    fun getAllUserInputsForRestaurant(restaurantId: Long): LiveData<List<UserInputWithRestaurant>> { //for displaying all posts for a certain restaurant
        return userInputRepository.getAllUserInputsForRestaurant(restaurantId).asLiveData()
    }


    fun getAllRestaurants(): LiveData<List<Restaurant>> { //for getting all restaurants for MAP
        return restaurantRepository.getAllRestaurants().asLiveData()
    }

    suspend fun getRestaurantById(restaurantId: Long) : Restaurant? {
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
        if(modelClass.isAssignableFrom(OldRestaurantViewModel::class.java))
            return OldRestaurantViewModel(restaurantRepository, userInputRepository) as T
        throw IllegalArgumentException("Error")
    }
}