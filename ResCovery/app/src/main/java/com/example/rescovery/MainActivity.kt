package com.example.rescovery

import android.Manifest
import android.content.Intent
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
    private lateinit var oldRestaurantViewModel: OldRestaurantViewModel
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
                R.id.navigation_home, R.id.navigation_search, R.id.navigation_message, R.id.navigation_profile
            )
        )
        navView.setupWithNavController(navController)

        // Handle Nav bar click
        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {

                // Go to Add Post Activity when add clicked
                R.id.navigation_add -> {
                    val intent = Intent(this, AddPostActivity::class.java)
                    startActivity(intent)
                    true
                }

                // Otherwise, show other fragments
                else -> {
                    navController.navigate(item.itemId)
                    true
                }
            }
        }

        // Handle Nav bar click
        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                // Go to Add Post Activity when add clicked
                R.id.navigation_add -> {
                    val intent = Intent(this, AddPostActivity::class.java)
                    startActivity(intent)
                    true
                }
                // Otherwise, show other fragments
                else -> {
                    navController.navigate(item.itemId)
                    true
                }
            }
        }

        //____ Database Stuff___//
        appDatabase = AppDatabase.getInstance(this)
        restaurantRepository = RestaurantRepository(appDatabase.restaurantDatabaseDao)
        userInputRepository = UserInputRepository(appDatabase.userInputDao, appDatabase.restaurantDatabaseDao)
        val factory = RestaurantFactory(restaurantRepository, userInputRepository)
        oldRestaurantViewModel = ViewModelProvider(this, factory).get(OldRestaurantViewModel::class.java)
        //insertData()

        val database = AppDatabase.getInstance(this)
        val userInputDao = database.userInputDao
        val restaurantDao = database.restaurantDatabaseDao

        // Add data to the database
        CoroutineScope(Dispatchers.IO).launch {
            restaurantDao.clearTable()
            val restaurants = RestaurantData.getRestaurants()
            restaurantDao.insertAll(restaurants)
        }
    }

    override fun onResume() {
        super.onResume()

        // Ensure BottomNavigationView highlights the correct item when returning to MainActivity
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        // Check the current destination and update the selected item
        val currentDestination = navController.currentDestination?.id
        if (currentDestination == R.id.navigation_home) {
            navView.selectedItemId = R.id.navigation_home
        } else if (currentDestination == R.id.navigation_search) {
            navView.selectedItemId = R.id.navigation_search
        } else if (currentDestination == R.id.navigation_message) {
            navView.selectedItemId = R.id.navigation_message
        } else if (currentDestination == R.id.navigation_profile) {
            navView.selectedItemId = R.id.navigation_profile
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