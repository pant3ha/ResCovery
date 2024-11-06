package com.example.rescovery

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.rescovery.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var restaurantViewModel: RestaurantViewModel //for manually entering list of restaurants


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
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        //____ Database Stuff___//
        restaurantViewModel = ViewModelProvider(this).get(RestaurantViewModel::class.java)
        insertData()
    }

    private fun insertData() {
        lifecycleScope.launch {
            insertRestaurant()
        }
    }

    //insert all restaurants in database
    private suspend fun insertRestaurant() {
        val restaurants = getRestaurants()
        restaurants.forEach { restaurant ->
            restaurantViewModel.insertRestaurant(restaurant)
        }
    }

    private fun getRestaurants(): List<Restaurant> {
        //list of all restaurants at SFU
        return listOf(
            Restaurant(
                id = 0,
                restaurantName = "Togo Sushi SFU",
                restaurantAddress = "9055 University High St #107, Burnaby, BC V5A 0A7",
                coordinates = "49.278030875123086, -122.90948943862892",
                overallRating = 5.0, //to be calculated
                description = "sushi restaurant",
                priceRange = "10-20",
                phoneNumber = "(604)428-9120",
                ),
            Restaurant(
                id = 0,
                restaurantName = "Tim Hortons",
                restaurantAddress = "9055 University High St, Burnaby, BC V5A 0A7",
                coordinates = "49.27802501574761, -122.90931306073554",
                overallRating = 5.0, //to be calculated
                description = "Coffee shop",
                priceRange = "1-10",
                phoneNumber = "(604)298-6343",
            ),
            Restaurant(
                id = 0,
                restaurantName = "Uncle Faith's Pizza",
                restaurantAddress = "9055 University High St Unit 108, Burnaby, BC V5A 0A7",
                coordinates = "49.27811306540786, -122.9099747749615",
                overallRating = 5.0, //to be calculated
                description = "Pizza restaurant",
                priceRange = "1-10",
                phoneNumber = "(604)564-6565",
            ),
            Restaurant(
                id = 0,
                restaurantName = "Chef Hung Taiwanese Beef Noodle",
                restaurantAddress = "9055 University High St #109, Burnaby, BC V5A 0A7",
                coordinates = "49.27817119567328, -122.91018134455996",
                overallRating = 5.0, //to be calculated
                description = "Taiwanese restaurant",
                priceRange = "10-20",
                phoneNumber = "(604)299-8548",
            ),
            Restaurant(
                id = 0,
                restaurantName = "A&W Canada",
                restaurantAddress = "9055 University High St, Burnaby, BC V5A 4X6",
                coordinates = "49.27824692805295, -122.91036366959594",
                overallRating = 5.0, //to be calculated
                description = "Hamburger restaurant",
                priceRange = "10-20",
                phoneNumber = "(604)299-2194",
            ),
            Restaurant(
                id = 0,
                restaurantName = "PHO 99",
                restaurantAddress = "8901 Cornerstone Mews, Burnaby, BC V5A 4Y6",
                coordinates = "49.27787569999999, -122.9123993283764",
                overallRating = 5.0, //to be calculated
                description = "Pho restaurant",
                priceRange = "10-20",
                phoneNumber = "(604)423-2699",
            ),
            Restaurant(
                id = 0,
                restaurantName = "BierCraft UniverCity",
                restaurantAddress = "8902 University High St, Burnaby, BC V5A 4X6",
                coordinates = "49.278151584875665, -122.9123722509961",
                overallRating = 5.0, //to be calculated
                description = "Restaurant",
                priceRange = "20-30",
                phoneNumber = "(250)853-2370",
            ),
            Restaurant(
                id = 0,
                restaurantName = "Bamboo Garden Restaurant",
                restaurantAddress = "8911 Cornerstone Mews, Burnaby, BC V5A 0B3",
                coordinates = "49.27794889271149, -122.91217879517464",
                overallRating = 5.0, //to be calculated
                description = "Chinese Restaurant",
                priceRange = "10-20",
                phoneNumber = "(604)298-9869",
            ),
            Restaurant(
                id = 0,
                restaurantName = "Pizza Hut",
                restaurantAddress = "Simon Fraser University, 8917 Cornerstone Mews, Burnaby, BC V5A 4Y7",
                coordinates = "49.278005787169405, -122.91192837798182",
                overallRating = 5.0, //to be calculated
                description = "Pizza restaurant",
                priceRange = "10-20",
                phoneNumber = "(604)299-6446",
            ),
            Restaurant(
                id = 0,
                restaurantName = "Donair Town",
                restaurantAddress = "8923 Cornerstone Mews, Burnaby, BC V5A 0B3",
                coordinates = "49.27783899761702, -122.91188044914615",
                overallRating = 5.0, //to be calculated
                description = "Turkish Restaurant",
                priceRange = "10-20",
                phoneNumber = "(604)298-8188",
            ),
            Restaurant(
                id = 0,
                restaurantName = "Steve's Poke Bar",
                restaurantAddress = "8931 Cornerstone Mews, Burnaby, BC V5A 4Y7",
                coordinates = "49.277865694034375, -122.91176959147468",
                overallRating = 5.0, //to be calculated
                description = "Hawaiian",
                priceRange = "10-20",
                phoneNumber = "(604)559-7653",
            ),
            Restaurant(
                id = 0,
                restaurantName = "Quesada Burritos & Tacos",
                restaurantAddress = "8961 Cornerstone Mews, Burnaby, BC V5A 4Y7",
                coordinates = "49.27789679518428, -122.91134330496757",
                overallRating = 5.0, //to be calculated
                description = "Mexican restaurant",
                priceRange = "10-20",
                phoneNumber = "(604)559-9900",
            ),
            Restaurant(
                id = 0,
                restaurantName = "Mad Chicken SFU",
                restaurantAddress = "8888 University Dr W, Burnaby, BC V5A 1S6",
                coordinates = "49.27748211313536, -122.91597150644075",
                overallRating = 5.0, //to be calculated
                description = "Traditional restaurant",
                priceRange = "10-20",
                phoneNumber = "(778)782-8184",
            ),
            Restaurant(
                id = 0,
                restaurantName = "Japarrito",
                restaurantAddress = "5-8910 University High St, Burnaby, BC V5A 4X6",
                coordinates = "49.278273699335585, -122.91203033247294",
                overallRating = 5.0, //to be calculated
                description = "Japanese restaurant",
                priceRange = "10-20",
                phoneNumber = "(604)423-3139",
            ),
            Restaurant(
                id = 0,
                restaurantName = "Gawon Express Korean Cuisine",
                restaurantAddress = "8968 University High St, Burnaby, BC V5A 0B3",
                coordinates = "49.278096392460284, -122.91148196448898",
                overallRating = 5.0, //to be calculated
                description = "Korean restaurant",
                priceRange = "10-20",
                phoneNumber = "(604)298-2925",
            ),
            Restaurant(
                id = 0,
                restaurantName = "SFU Dining Commons",
                restaurantAddress = "8888 University Dr W, Burnaby, BC V5A 1S6",
                coordinates = "49.2799233703005, -122.92473116272903",
                overallRating = 5.0, //to be calculated
                description = "Buffet restaurant",
                priceRange = "20-30",
                phoneNumber = "(778)782-7047",
            ),
            Restaurant(
                id = 0,
                restaurantName = "Plum Garden & CHAKURA",
                restaurantAddress = "8939 and 8951 Cornerstone Mews, Burnaby, BC V5A 4Y6",
                coordinates = "49.27797328607641, -122.91171166448898",
                overallRating = 5.0, //to be calculated
                description = "Restaurant",
                priceRange = "10-20",
                phoneNumber = "(604)782-2733",
            ),
            Restaurant(
                id = 0,
                restaurantName = "Noodle Waffle Cafe",
                restaurantAddress = "Maggie Benston Student Service Centre, 8888 University High St, Burnaby, BC V5A 0A9",
                coordinates = "49.27880088636505, -122.91896583014749",
                overallRating = 5.0, //to be calculated
                description = "Restaurant",
                priceRange = "10-20",
                phoneNumber = "(604)353-7582",
            ),
            Restaurant(
                id = 0,
                restaurantName = "Subway",
                restaurantAddress = "The Cornerstone Bldg, 8916 University High St Unit R6, Burnaby, BC V5A 4Y6",
                coordinates = "49.278078009663055, -122.91204724641572",
                overallRating = 5.0, //to be calculated
                description = "Sandwich shop",
                priceRange = "10-20",
                phoneNumber = "(604)205-5854",
            ),
        )
    }
}
