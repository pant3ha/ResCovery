package com.example.rescovery.events

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.example.rescovery.AppDatabase
import com.example.rescovery.Globals
import com.example.rescovery.R
import com.example.rescovery.Restaurant
import com.example.rescovery.RestaurantRepository
import com.example.rescovery.adapters.RestaurantsListAdapter
import com.example.rescovery.ui.home.fragments.map.MapViewModel
import kotlinx.coroutines.launch

class ChooseRestaurantActivity : AppCompatActivity() {

    private lateinit var searchBar: EditText
    private lateinit var restaurantsListView: ListView

    private lateinit var restaurantRepo: RestaurantRepository
    private lateinit var restaurants: ArrayList<Restaurant>
    private lateinit var restaurantsShown: ArrayList<Restaurant>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_restaurant)

        restaurantRepo = RestaurantRepository(AppDatabase.getInstance(application).restaurantDatabaseDao)
        restaurants = ArrayList()
        restaurantsShown = ArrayList()

        // Array adapter
        restaurantsListView = findViewById(R.id.choose_restaurant_list)
        val restaurantsListAdapter = RestaurantsListAdapter(this, R.layout.listadapter_restaurant,restaurantsShown)
        restaurantsListView.adapter = restaurantsListAdapter

        restaurantRepo.getAllRestaurants().asLiveData().observe(this) { restaurantList ->
            if (restaurantList != null) {
                restaurants.clear()
                restaurants.addAll(restaurantList)

                restaurantsShown.clear()
                restaurantsShown.addAll(restaurantList)

                restaurantsListAdapter.notifyDataSetChanged()
            }
        }

        searchBar = findViewById(R.id.choose_restaurant_search)
        searchBar.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                println(s)
                restaurantsShown.clear()
                if(s.isNullOrEmpty()) {
                    restaurantsShown.addAll(restaurants)
                } else {
                    restaurantsShown.addAll(restaurants.filter { it.restaurantName.contains(s, ignoreCase = true) })
                }
                restaurantsListAdapter.notifyDataSetChanged()
            }

        })

        // On restaurant selection
        restaurantsListView.setOnItemClickListener { _, _, position, _ ->
            val selectedRestaurant = restaurantsShown[position]
            val resultIntent = Intent().apply {
                putExtra(Globals.EVENT_RESTAURANT_KEY, selectedRestaurant)
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }
}