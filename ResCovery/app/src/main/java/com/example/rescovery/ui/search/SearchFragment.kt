package com.example.rescovery.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.rescovery.AppDatabase
import com.example.rescovery.R
import com.example.rescovery.databinding.FragmentSearchBinding
import com.example.rescovery.ui.home.fragments.RestaurantFragment
import com.example.rescovery.ui.home.fragments.RestaurantViewModel
import com.example.rescovery.ui.home.fragments.RestaurantViewModelFactory
import com.google.firebase.database.FirebaseDatabase

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var searchViewModel: SearchViewModel
    private lateinit var restaurantViewModel: RestaurantViewModel
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val restaurantDao = AppDatabase.getInstance(requireContext()).restaurantDatabaseDao
        restaurantViewModel = ViewModelProvider(
            this,
            RestaurantViewModelFactory(restaurantDao, FirebaseDatabase.getInstance())
        )[RestaurantViewModel::class.java]
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        searchViewModel =
            ViewModelProvider(this).get(SearchViewModel::class.java)

        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val root: View = binding.root

        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, mutableListOf())
        binding.listView.adapter = adapter

        // Observe filtered restaurant list
        searchViewModel.filteredRestaurants.observe(viewLifecycleOwner) { restaurants ->
            adapter.clear()
            adapter.addAll(restaurants)
            adapter.notifyDataSetChanged()
        }

        // Open corresponding restaurant page when list clicked
        binding.listView.setOnItemClickListener { _, _, position, _ ->
            val restaurantName = adapter.getItem(position)
            val restaurantId = getRestaurantIdByName(restaurantName) // Convert name to ID
            openRestaurantFragment(restaurantId)
        }

        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchViewModel.filter(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchViewModel.filter(newText)
                return true
            }

        })

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getRestaurantIdByName(name: String?): Int {
        val restaurantArray = resources.getStringArray(R.array.restaurant_names_spinner)
        return restaurantArray.indexOf(name)
    }

    private fun openRestaurantFragment(restaurantId: Int) {
        binding.searchPage.visibility = View.GONE
        binding.fragmentContainer.visibility = View.VISIBLE

        val restaurantFragment = RestaurantFragment.newInstance(restaurantId, openedFromSearch = true)
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, restaurantFragment)  // Make sure the correct container is used
            .addToBackStack(null)
            .commit()
    }

}