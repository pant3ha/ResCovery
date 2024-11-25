package com.example.rescovery

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.rescovery.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var restaurantViewModel: RestaurantViewModel //for manually entering list of restaurants
    private lateinit var restaurantRepository: RestaurantRepository
    private lateinit var userInputRepository: UserInputRepository
    private lateinit var appDatabase: AppDatabase


    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                //R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        //____ Database Stuff___//
        appDatabase = AppDatabase.getInstance(this)
        restaurantRepository = RestaurantRepository(appDatabase.restaurantDatabaseDao)
        userInputRepository = UserInputRepository(appDatabase.userInputDao, appDatabase.restaurantDatabaseDao)
        val factory = RestaurantFactory(restaurantRepository, userInputRepository)
        restaurantViewModel = ViewModelProvider(this, factory).get(RestaurantViewModel::class.java)
        //insertData()

        val database = AppDatabase.getInstance(this)
        val userInputDao = database.userInputDao
        val restaurantDao = database.restaurantDatabaseDao

        // Add data to the database
        CoroutineScope(Dispatchers.IO).launch {
            val posts = PostData.getPosts()
            val restaurants = RestaurantData.getRestaurants()
            restaurantDao.insertAll(restaurants)
            userInputDao.insertAll(posts)
            Log.d("MainActivity", "added default posts to database: $posts")
        }
    }




    /*private fun insertData() {
        lifecycleScope.launch {
            insertRestaurant()
        }
    }

    //insert all restaurants in database
    private suspend fun insertRestaurant() {
        val restaurants = RestaurantData.getRestaurants()
        restaurants.forEach { restaurant ->
            restaurantViewModel.insertRestaurant(restaurant)
        }
    }*/

}