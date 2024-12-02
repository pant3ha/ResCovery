package com.example.rescovery.ui.profile

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rescovery.AccountSettingsActivity
import com.example.rescovery.AppDatabase
import com.example.rescovery.Globals
import com.example.rescovery.ImageUtils
import com.example.rescovery.ImageUtils.decode
import com.example.rescovery.LoginActivity
import com.example.rescovery.R
import com.example.rescovery.adapters.PostAdapter
import com.example.rescovery.data.User
import com.example.rescovery.databinding.FragmentProfileBinding
import com.example.rescovery.ui.home.fragments.RestaurantFragment
import com.example.rescovery.ui.home.fragments.RestaurantViewModel
import com.example.rescovery.ui.home.fragments.RestaurantViewModelFactory
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PostAdapter
    private lateinit var viewModel: ProfileViewModel
    private lateinit var restaurantViewModel: RestaurantViewModel
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val restaurantDao = AppDatabase.getInstance(requireContext()).restaurantDatabaseDao
        restaurantViewModel = ViewModelProvider(this, RestaurantViewModelFactory(restaurantDao, FirebaseDatabase.getInstance())
        )[RestaurantViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)
        viewModel = profileViewModel

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Get info from database
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("users")

        //set up posts recycler
        recyclerView = root.findViewById(R.id.posts_container)!!
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        adapter = PostAdapter(emptyList(), restaurantViewModel)
        recyclerView.adapter = adapter

        // Get current user
        val currentUserPref = requireActivity().getSharedPreferences(Globals.PREF_CUR_USER_NAME, MODE_PRIVATE)
        val username = currentUserPref.getString(Globals.PREF_CUR_USER_KEY, "").toString()
        println(username)

        viewModel.user.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                binding.userName.text = user.fullName
                binding.userUsername.text = "@${user.username}"
                binding.userBio.text = user.bio

                //use ImageUtils to decode from string
                val decodedBitmap = decode(user.profileImage ?: "")

                // Check if decoding was successful
                if (decodedBitmap != null) {
                    binding.imgProfile.setImageBitmap(decodedBitmap)
                } else {
                    Glide.with(this@ProfileFragment)
                        .load(R.drawable.profile)    // Use default profile image
                        .into(binding.imgProfile)
                }

            }
        }

        // Get user info
        if (viewModel.user.value == null) {
            lifecycleScope.launch {
                viewModel.getUser(username)
            }
        }

        // Get user posts
        if (viewModel.posts.value.isNullOrEmpty()) {
            viewModel.getUserPosts(username)
        }

        // Go to Edit Profile when button clicked
        binding.editProfile.setOnClickListener {
            val intent = Intent(context, AccountSettingsActivity::class.java)
            startActivity(intent)
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.posts.observe(viewLifecycleOwner) { posts ->
            if (posts.isEmpty()) {
                Toast.makeText(requireContext(), "No posts available.", Toast.LENGTH_SHORT).show()
            } else {
                adapter.updateData(posts) // Send the data to the adapter
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /*private fun openRestaurantFragment(restaurantId: Int) {
        val restaurantFragment = RestaurantFragment.newInstance(restaurantId)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, restaurantFragment)
            .addToBackStack(null)
            .commit()
    }*/
}