package com.example.rescovery.ui.home.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.example.rescovery.AppDatabase
import com.example.rescovery.ImageUtils
import com.example.rescovery.R
import com.example.rescovery.Restaurant
import com.example.rescovery.RestaurantDatabaseDao
import com.example.rescovery.adapters.PostAdapter
import com.example.rescovery.data.Post
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson



class RestaurantFragment : Fragment() {
    private var restaurantId: Int? = null
    private lateinit var closeBtn: Button
    private lateinit var imageAdapter: RestaurantImageAdapter
    private lateinit var commentAdapter: RestaurantCommentAdapter
    private lateinit var recycler: RecyclerView
    private lateinit var textRecycler: RecyclerView
    private lateinit var restaurantDao: RestaurantDatabaseDao
    private val viewModel: RestaurantViewModel by lazy {
        RestaurantViewModelFactory(restaurantDao, FirebaseDatabase.getInstance()).create(RestaurantViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        restaurantId = arguments?.getInt(ARG_RESTAURANT)
        restaurantDao = AppDatabase.getInstance(requireContext()).restaurantDatabaseDao
        Log.d("RestaurantFragment", "Received restaurant: $restaurantId")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_restaurant, container, false)

        //set up image recycler
        recycler = view.findViewById(R.id.image_scroll)
        recycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        imageAdapter = RestaurantImageAdapter(
            posts = emptyList(),
            onItemClick = { post ->
                val postFragment = PostFragment.newInstance(post)
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, postFragment)
                    .addToBackStack(null)
                    .commit()
            }
        )
        recycler.adapter = imageAdapter

        //set up comment recycler
        textRecycler = view.findViewById(R.id.comments)
        textRecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        commentAdapter = RestaurantCommentAdapter(
            posts = emptyList(),
            onItemClick = { post ->
                val postFragment = PostFragment.newInstance(post)
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, postFragment)
                    .addToBackStack(null)
                    .commit()
            }
        )
        textRecycler.adapter = commentAdapter

        //close button
        closeBtn = view.findViewById(R.id.close_btn)
        closeBtn.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //get restaurant id from arguments passed
        val restaurantId = arguments?.getInt(ARG_RESTAURANT) ?: return
        //use viewmodel functions to get fields of restaurant and post
        viewModel.getRestaurantDetails(restaurantId)
        viewModel.getPostsForRestaurant(restaurantId)
        //dynamically display the fields
        viewModel.restaurant.observe(viewLifecycleOwner) { restaurant ->
            view.findViewById<TextView>(R.id.name)?.text = restaurant?.restaurantName ?: "Unknown"
            view.findViewById<TextView>(R.id.address)?.text = restaurant?.restaurantAddress ?: "Unknown"
            view.findViewById<TextView>(R.id.description)?.text = restaurant?.description ?: "Unknown"
            view.findViewById<TextView>(R.id.price_range)?.text = restaurant?.priceRange ?: "Unknown"
            view.findViewById<TextView>(R.id.phone)?.text = restaurant?.phoneNumber ?: "Unknown"
            view.findViewById<RatingBar>(R.id.overall_rating).rating = (restaurant?.overallRating ?: 5.0).toFloat()
        }

        Log.d("RestaurantFragment", "starting post receiving")
        //responsible for getting images and comment posts for a restaurant
        viewModel.posts.observe(viewLifecycleOwner, Observer { posts ->
            Log.d("RestaurantFragment", "Posts received: ${posts.size}")
            val imagePosts = posts.filter { !it.image.isNullOrBlank() }
            Log.d("RestaurantFragment", "Image posts: ${imagePosts.size}")

            //call update data to load images
            imageAdapter.updateData(imagePosts)

            val commentPosts = posts.filter { !it.review.isNullOrBlank()}
            Log.d("RestaurantFragment", "Comment posts: ${commentPosts.size}")
            commentAdapter.updateData(commentPosts)
        })
    }


    companion object {
        private const val ARG_RESTAURANT = "arg_restaurant"
        fun newInstance(restaurantId: Int) : RestaurantFragment {
            val fragment = RestaurantFragment()
            val args = Bundle()
            args.putInt(ARG_RESTAURANT, restaurantId)
            fragment.arguments = args
            return fragment
        }
    }

}