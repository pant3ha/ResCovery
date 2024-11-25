package com.example.rescovery.ui.home.fragments

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rescovery.Restaurant
import com.example.rescovery.UserInput
import com.example.rescovery.UserInputDao
import com.google.gson.Gson
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch

class RestaurantViewModel (private val userInputDao: UserInputDao) : ViewModel() {
    fun getUserInputsForRestaurant(restaurantId: Long, onResult: (List<UserInput>) -> Unit) {
        viewModelScope.launch {
            val userInputs = userInputDao.getAllUserInputsForRestaurant(restaurantId).firstOrNull()
            onResult(userInputs?.map { it.userInput} ?: emptyList())
        }
    }
    fun getImagesForRestaurant(restaurantId: Long, onResult: (List<String>) -> Unit) {
        Log.d("RestaurantViewModel", "Fetching images for restaurantId: $restaurantId")
        viewModelScope.launch {
            val userInputs = userInputDao.getAllUserInputsForRestaurant(restaurantId).firstOrNull()
            if (userInputs.isNullOrEmpty()) {
                Log.d("RestaurantViewModel", "No user inputs found for restaurantId: $restaurantId")
            } else { Log.d("RestaurantViewModel", "User inputs fetched: $userInputs") }
            val userImages = userInputs?.mapNotNull { it.userInput.photoUri } ?: emptyList()
            Log.d("RestaurantViewModel", "User images extracted: $userImages")
            onResult(userImages)
        }
    }

}