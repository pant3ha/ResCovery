package com.example.rescovery.ui.home.fragments.discovery

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.helper.widget.Grid
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rescovery.R
import com.example.rescovery.adapters.PostAdapter
import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rescovery.AppDatabase
import com.example.rescovery.post_data.Post
import com.example.rescovery.ui.home.fragments.PostFragment
import com.example.rescovery.ui.home.fragments.RestaurantFragment
import com.example.rescovery.ui.home.fragments.RestaurantViewModel
import com.example.rescovery.ui.home.fragments.RestaurantViewModelFactory
import com.google.firebase.database.FirebaseDatabase

class DiscoveryFragment : Fragment() {

    companion object {
        fun newInstance() = DiscoveryFragment()
    }

    private val viewModel: DiscoveryViewModel by viewModels()
    private lateinit var restaurantViewModel: RestaurantViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val restaurantDao = AppDatabase.getInstance(requireContext()).restaurantDatabaseDao
        restaurantViewModel = ViewModelProvider(this, RestaurantViewModelFactory(restaurantDao, FirebaseDatabase.getInstance())
        )[RestaurantViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_discovery, container, false)
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        adapter = PostAdapter(emptyList(), restaurantViewModel) { post ->
            post.restaurant?.let { restaurant ->
                openRestaurantFragment(restaurant)
            }
        }
        recyclerView.adapter = adapter

        // Show post details activity
//        adapter.onPostClick = {
//            val intent = Intent(requireContext(), PostDetailsActivity::class.java)
//            intent.putExtra("post", it)
//            startActivity(intent)
//        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.posts.observe(viewLifecycleOwner, Observer { posts ->
            adapter.updateData(posts)
        })
    }

    private fun openRestaurantFragment(restaurantId: Int) {
        val restaurantFragment = RestaurantFragment.newInstance(restaurantId)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, restaurantFragment)
            .addToBackStack(null)
            .commit()
    }
}