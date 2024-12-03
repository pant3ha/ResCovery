package com.example.rescovery.ui.home.fragments

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.rescovery.Restaurant
import com.example.rescovery.UserInput
import androidx.lifecycle.viewModelScope
import androidx.privacysandbox.ads.adservices.adid.AdId
import com.example.rescovery.RestaurantDatabaseDao
import com.example.rescovery.UserInputDao
import com.example.rescovery.data.Post
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch

//responsible for getting details for restaurant from firebase and room database
class RestaurantViewModel (private val restaurantDao: RestaurantDatabaseDao, private val firebaseDatabase: FirebaseDatabase) : ViewModel() {

    private val _restaurant = MutableLiveData<Restaurant?>()
    val restaurant: MutableLiveData<Restaurant?> get() = _restaurant
    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> get() = _posts
    private val cache = mutableMapOf<Int, Restaurant?>()

    fun getRestaurantDetailsCache(restaurantId: Int): LiveData<Restaurant?> {
        val liveData = MutableLiveData<Restaurant?>()
        if(cache.containsKey(restaurantId)) {
            liveData.postValue(cache[restaurantId])
        } else {
            viewModelScope.launch {
                val restaurant = restaurantDao.getRestaurantById(restaurantId.toLong())
                cache[restaurantId] = restaurant
                liveData.postValue(restaurant)
            }
        }
        return liveData
    }
    //get restaurant details:
    fun getRestaurantDetails(restaurantId: Int) {
        viewModelScope.launch {
            val restaurant = restaurantDao.getRestaurantById(restaurantId.toLong())
            if (restaurant != null) {
                _restaurant.postValue(restaurant)
            } else {
                Log.e("RestaurantViewModel", "No restaurant found for: $restaurantId")
                _restaurant.postValue(null)
            }
        }
    }

    //get posts related to specific restaurant from firebase
    fun getPostsForRestaurant(restaurantId: Int) {
        Log.d("RestaurantViewModel", "Querying posts for restaurantId: $restaurantId")

        val postsRef = FirebaseDatabase.getInstance().getReference("posts")
        postsRef.orderByChild("restaurant").equalTo(restaurantId.toDouble()).get()
            .addOnSuccessListener { snapshot ->
                Log.d("RestaurantViewModel", "Firebase snapshot exists: ${snapshot.exists()}")
                if (snapshot.exists()) {
                    val postList = mutableListOf<Post>()
                    for (child in snapshot.children) {
                        val post = child.getValue(Post::class.java)
                        Log.d("RestaurantViewModel", "Post retrieved: ${post?.restaurant}")
                        if (post != null) postList.add(post)
                    }
                    Log.d("RestaurantViewModel", "Posts fetched: ${postList.size}")
                    _posts.value = postList
                    //for rating
                    updateOverallRating(restaurantId, postList)
                } else {
                    Log.d("RestaurantViewModel", "No posts found for restaurantId: $restaurantId")
                    _posts.value = emptyList()
                }
            }
            .addOnFailureListener { error ->
                Log.e("RestaurantViewModel", "Error fetching posts: ${error.message}")
                _posts.value = emptyList()
            }
    }

    /*fun getUserInputsForRestaurant(restaurantId: Long, onResult: (List<UserInput>) -> Unit) {
        viewModelScope.launch {
            val userInputs = userInputDao.getAllUserInputsForRestaurant(restaurantId).firstOrNull()
            onResult(userInputs?.map { it.userInput} ?: emptyList())
        }
    }*/
    /*fun getImagesForRestaurant(restaurantId: Long, onResult: (List<String>) -> Unit) {
        Log.d("RestaurantViewModel", "Fetching images for restaurantId: $restaurantId")
        viewModelScope.launch {
            val userInputs = userInputDao.getAllUserInputsForRestaurant(restaurantId).firstOrNull()
            if (userInputs.isNullOrEmpty()) {
                Log.d("RestaurantViewModel", "No user inputs found for restaurantId: $restaurantId")
            } else {
                Log.d("RestaurantViewModel", "User inputs fetched: $userInputs")
            }
            val userImages = userInputs?.mapNotNull { it.userInput.photoUri } ?: emptyList()
            Log.d("RestaurantViewModel", "User images extracted: $userImages")
        }
    }*/

    fun updateOverallRating(restaurantId: Int, posts: List<Post>) {
        viewModelScope.launch {
            // Calculate the average rating
            val averageRating = if (posts.isNotEmpty()) {
                posts.mapNotNull { it.rating }.average()
            } else {
                0.0 //Default rating if no posts exist
            }

            //Update the restaurant's overall rating in the database
            val restaurant = restaurantDao.getRestaurantById(restaurantId.toLong())
            if (restaurant != null) {
                restaurant.overallRating = averageRating
                restaurantDao.updateRestaurant(restaurant)
                _restaurant.postValue(restaurant)
            }
        }
    }
}

//factory:
class RestaurantViewModelFactory(private val restaurantDao: RestaurantDatabaseDao, private val firebaseDatabase: FirebaseDatabase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>) : T {
        if (modelClass.isAssignableFrom(RestaurantViewModel::class.java)) {
            return RestaurantViewModel(restaurantDao, firebaseDatabase) as T
        }
        throw IllegalArgumentException("UNKNOWN VIEWMODEL CLASS")
    }
}