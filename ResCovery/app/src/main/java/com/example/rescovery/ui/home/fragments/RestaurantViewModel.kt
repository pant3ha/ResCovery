package com.example.rescovery.ui.home.fragments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rescovery.Restaurant
import com.example.rescovery.UserInputDao
import com.google.gson.Gson
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch

class RestaurantViewModel (private val userInputDao: UserInputDao) : ViewModel() {
    fun getImagesForRestaurant(restaurant: Restaurant, onResult: (List<String>) -> Unit) {
        viewModelScope.launch {
            val userInputs = userInputDao.getAllUserInputsForRestaurant(restaurant.id).firstOrNull()
            val userImages = userInputs?.mapNotNull { it.userInput.photoUri } ?: emptyList()
            onResult(userImages)
        }
    }

}