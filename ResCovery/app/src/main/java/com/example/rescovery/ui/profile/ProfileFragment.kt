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
import androidx.lifecycle.ViewModelProvider
import com.example.rescovery.AccountSettingsActivity
import com.example.rescovery.Globals
import com.example.rescovery.ImageUtils
import com.example.rescovery.LoginActivity
import com.example.rescovery.R
import com.example.rescovery.databinding.FragmentProfileBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var database: FirebaseDatabase
    private lateinit var usersRef: DatabaseReference
    private lateinit var username: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Get info from database
        database = FirebaseDatabase.getInstance()
        usersRef = database.getReference("users")

        // Get current user
        val currentUserPref = requireActivity().getSharedPreferences(Globals.PREF_CUR_USER_NAME, MODE_PRIVATE)
        username = currentUserPref.getString(Globals.PREF_CUR_USER_KEY, "").toString()

        loadUserData()

        // Go to Edit Profile when button clicked
        binding.editProfile.setOnClickListener {
            val intent = Intent(context, AccountSettingsActivity::class.java)
            startActivity(intent)
        }

        return root
    }

    private fun loadUserData() {

        usersRef.child(username).get().addOnSuccessListener { snapshot ->
            binding.userName.text = snapshot.child("fullName").value.toString()
            binding.userUsername.text = "@$username"
            if(snapshot.child("bio").exists()) {
                binding.userBio.text = snapshot.child("bio").value.toString()
            }
            else{
                binding.userBio.text = ""
            }

            // Load profile image
            val image = snapshot.child("profileImage").value.toString()
            val profileBitmap = ImageUtils.decode(image)
            if (profileBitmap != null) {
                binding.imgProfile.setImageBitmap(profileBitmap)
            } else {
                binding.imgProfile.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.profile))
            }
        }.addOnFailureListener {
            Toast.makeText(requireActivity(), "Failed to access database", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}