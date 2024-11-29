package com.example.rescovery.ui.home.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.example.rescovery.AppDatabase
import com.example.rescovery.R
import com.example.rescovery.Restaurant
import com.google.gson.Gson



class RestaurantFragment : Fragment() {
    private lateinit var restaurant: Restaurant
    private lateinit var closeBtn: Button
    private lateinit var imageAdapter: RestaurantImageAdapter
    private lateinit var recycler: RecyclerView
    private lateinit var textRecycler: RecyclerView
    private lateinit var restaurantViewModel: RestaurantViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            restaurant = it.getParcelable(ARG_RESTAURANT)!!
        }
        Log.d("RestaurantFragment", "Received restaurant: $restaurant")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_restaurant, container, false)
        recycler = view.findViewById(R.id.image_scroll)
        recycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        textRecycler = view.findViewById(R.id.comments)
        textRecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        val userInputDao = AppDatabase.getInstance(requireContext()).userInputDao
        restaurantViewModel = RestaurantViewModel(userInputDao)

        restaurantViewModel.getUserInputsForRestaurant(restaurant.id) { userInputs ->
            imageAdapter = RestaurantImageAdapter(userInputs, restaurant) { userInput ->
                val postFragment = PostFragment.newInstance(restaurant, userInput)
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, postFragment)
                    .addToBackStack(null)
                    .commit()
            }
            recycler.adapter = imageAdapter

            val commentAdapter = RestaurantCommentAdapter(userInputs, restaurant) { userInput ->
                val postFragment = PostFragment.newInstance(restaurant, userInput)
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, postFragment)
                    .addToBackStack(null)
                    .commit()

            }
            textRecycler.adapter = commentAdapter
        }

        //get images from uerInputs and from default restaurant images
        /*Log.d("RestaurantFragment", "Initializing ViewModel")
        val userInputDao = AppDatabase.getInstance(requireContext()).userInputDao
        restaurantViewModel = RestaurantViewModel(userInputDao)
        Log.d("RestaurantFragment", "Fetching images for restaurantId: ${restaurant.id}")
        restaurantViewModel.getImagesForRestaurant(restaurant.id) { imageUrls ->
            Log.d("RestaurantFragment", "Fetched image URLs: $imageUrls")
            if(imageUrls.isEmpty()) {
                Log.d("RestaurantFragment", "No images to display")
            }
            imageAdapter = RestaurantImageAdapter(imageUrls)
            recycler.adapter = imageAdapter
        }*/
        //get images from uerInputs and from default restaurant images
        /*val userInputDao = AppDatabase.getInstance(requireContext()).userInputDao
        restaurantViewModel = RestaurantViewModel(userInputDao)
        restaurantViewModel.getImagesForRestaurant(restaurant) { imageUrls ->
            imageAdapter = RestaurantImageAdapter(imageUrls)
            recycler.adapter = imageAdapter
        }*/
        //imageAdapter = RestaurantImageAdapter(parseImageUrls(restaurant.imageUrls))
        //recycler.adapter = imageAdapter
        view.findViewById<TextView>(R.id.name).text = restaurant.restaurantName
        view.findViewById<TextView>(R.id.address).text = restaurant.restaurantAddress
        view.findViewById<TextView>(R.id.description).text = restaurant.description
        view.findViewById<TextView>(R.id.price_range).text = restaurant.priceRange
        view.findViewById<TextView>(R.id.phone).text = restaurant.phoneNumber
        closeBtn = view.findViewById(R.id.close_btn)


        // for testing
        /*
        val defaultImages = listOf(
            "android.resource://${requireContext().packageName}/${R.drawable.placeholder_image}",
            "android.resource://${requireContext().packageName}/${R.drawable.placeholder_image}",
        )
        imageAdapter = RestaurantImageAdapter(defaultImages)
        recycler.adapter = imageAdapter
        view.findViewById<TextView>(R.id.name).text = "Test Restaurant"
        view.findViewById<TextView>(R.id.address).text = "123 Placeholder St."
        view.findViewById<TextView>(R.id.description).text = "A test restaurant to showcase the RecyclerView scrolling." */

        closeBtn.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        return view
    }

    //not needed anymore
    /*private fun parseImageUrls(jsonString: String): List<String> {
    private fun parseImageUrls(jsonString: String): List<String> {
        if (jsonString.isNullOrEmpty()) {
            return emptyList() // Return an empty list if JSON string is null or empty
        }
        return try {
            Gson().fromJson(jsonString, Array<String>::class.java).toList()
        } catch (e: Exception) {
            Log.e("RestaurantFragment", "Failed to parse string to json", e)
            emptyList()
        }

    }*/

    companion object {
        private const val ARG_RESTAURANT = "arg_restaurant"
        fun newInstance(restaurant: Restaurant) : RestaurantFragment {
            val fragment = RestaurantFragment()
            val args = Bundle()
            args.putParcelable(ARG_RESTAURANT, restaurant)
            fragment.arguments = args
            return fragment
        }
    }

}