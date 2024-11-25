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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rescovery.AppDatabase
import com.example.rescovery.R
import com.example.rescovery.Restaurant
import com.example.rescovery.UserInput


class PostFragment : Fragment() {
    private lateinit var restaurant: Restaurant
    private lateinit var userInput: UserInput
    private lateinit var backBtn: Button
    private lateinit var imageAdapter: RestaurantImageAdapter
    private lateinit var recycler: RecyclerView
    private lateinit var restaurantViewModel: RestaurantViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            restaurant = it.getParcelable(ARG_RESTAURANT)!!
            userInput = it.getParcelable(ARG_USER_INPUT)!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_post, container, false)
        recycler = view.findViewById(R.id.photo_container)
        recycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)


        view.findViewById<TextView>(R.id.comments).text = userInput.comment
        view.findViewById<TextView>(R.id.user_name).text = userInput.userName
        view.findViewById<TextView>(R.id.restaurant_name).text = restaurant.restaurantName
        view.findViewById<TextView>(R.id.restaurant_address).text = restaurant.restaurantAddress
        view.findViewById<RatingBar>(R.id.rating).rating = userInput.rating.toFloat()
        backBtn = view.findViewById(R.id.back_btn)

        backBtn.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }


        return view
    }

    companion object {
        private const val ARG_RESTAURANT = "arg_restaurant"
        private const val ARG_USER_INPUT = "arg_user_input"
        fun newInstance(restaurant: Restaurant, userInput: UserInput) : PostFragment {
            val fragment = PostFragment()
            val args = Bundle()
            args.putParcelable(ARG_RESTAURANT, restaurant)
            args.putParcelable(ARG_USER_INPUT, userInput)
            fragment.arguments = args
            return fragment
        }
    }
}