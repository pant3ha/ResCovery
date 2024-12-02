package com.example.rescovery.ui.home.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rescovery.AppDatabase
import com.example.rescovery.ImageUtils
import com.example.rescovery.R
import com.example.rescovery.Restaurant
import com.example.rescovery.UserInput
import com.example.rescovery.post_data.Post
import com.google.firebase.database.FirebaseDatabase


class PostFragment : Fragment() {
    private lateinit var restaurant: Restaurant
    private lateinit var post: Post
    private lateinit var backBtn: ImageButton
    private lateinit var imageAdapter: RestaurantImageAdapter
    private lateinit var recycler: RecyclerView
    private lateinit var restaurantViewModel: RestaurantViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            post = it.getParcelable(ARG_POST)!!
        }

        //set up databases
        val restaurantDao = AppDatabase.getInstance(requireContext()).restaurantDatabaseDao
        restaurantViewModel = ViewModelProvider(this, RestaurantViewModelFactory(restaurantDao, FirebaseDatabase.getInstance())
        )[RestaurantViewModel::class.java]

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_post, container, false)


        //display user post details
        view.findViewById<TextView>(R.id.comments).text = post.review
        view.findViewById<TextView>(R.id.user_name).text = post.publisher
        view.findViewById<RatingBar>(R.id.rating).rating = post.rating!!

        //display image of post
        val postImage = view.findViewById<ImageView>(R.id.photo_container)
        post.image?.let { base64 ->
            val bitmap = ImageUtils.decode(base64)
            if (bitmap != null) {
                postImage.setImageBitmap(bitmap)
            } else {
                postImage.setImageResource(R.drawable.placeholder_image)
            }
        }
        backBtn = view.findViewById(R.id.back_btn)

        backBtn.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        //display restaurant details:
        post.restaurant?.let { restaurantId ->
            restaurantViewModel.getRestaurantDetails(restaurantId)
            restaurantViewModel.restaurant.observe(viewLifecycleOwner) { restaurant ->
                if (restaurant != null) {
                    view.findViewById<TextView>(R.id.restaurant_name).text = restaurant.restaurantName
                    view.findViewById<TextView>(R.id.restaurant_address).text = restaurant.restaurantAddress
                } else {
                    view.findViewById<TextView>(R.id.restaurant_name).text = "Unknown Restaurant"
                    view.findViewById<TextView>(R.id.restaurant_address).text = "Unknown address"
                }
            }
        }


        return view
    }

    //takes argument post of type Post so it can display the appropriate details of post
    companion object {
        private const val ARG_POST = "arg_post"
        fun newInstance(post: Post) : PostFragment {
            val fragment = PostFragment()
            val args = Bundle()
            args.putParcelable(ARG_POST, post)
            fragment.arguments = args
            return fragment
        }
    }
}