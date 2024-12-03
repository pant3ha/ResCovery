package com.example.rescovery.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SearchViewModel : ViewModel() {

    // List of restaurants
    private val restaurantList = listOf(
        "A&W Canada",
        "Bamboo Garden Restaurant",
        "BierCraft UniverCity",
        "Chef Hung Taiwanese Beef Noodle",
        "Donair Town",
        "Gawon Express Korean Cuisine",
        "Japarrito",
        "Mad Chicken SFU",
        "Noodle Waffle Cafe",
        "PHO 99",
        "Pizza Hut",
        "Plum Garden & CHAKURA",
        "Quesada Burritos & Tacos",
        "SFU Dining Commons",
        "Steve's Poke Bar",
        "Subway",
        "Tim Hortons",
        "Togo Sushi SFU",
        "Uncle Faith's Pizza"
    )

    private val _filteredRestaurants = MutableLiveData<List<String>>()
    val filteredRestaurants: LiveData<List<String>> get() = _filteredRestaurants

    init {
        _filteredRestaurants.value = restaurantList
    }

    fun filter(query: String?) {
        val queryLower = query?.lowercase() ?: ""
        _filteredRestaurants.value = if (queryLower.isEmpty()) {
            restaurantList
        } else {
            restaurantList.filter { it.lowercase().contains(queryLower) }
        }
    }
}