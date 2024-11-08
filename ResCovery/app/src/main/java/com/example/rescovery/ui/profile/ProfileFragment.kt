package com.example.rescovery.ui.profile

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.rescovery.Globals
import com.example.rescovery.MainActivity
import com.example.rescovery.databinding.FragmentProfileBinding
import com.example.rescovery.profile_stuff.LoginActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlin.system.exitProcess

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val usernameText = binding.profileUsername
        val passwordText = binding.profilePassword
        val logoutBtn = binding.profileLogoutBtn

        // Get info from database
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val usersRef: DatabaseReference = database.getReference("users")

        // Get current user
        val currentUserPref = requireActivity().getSharedPreferences(Globals.PREF_CUR_USER_NAME, MODE_PRIVATE)
        val username = currentUserPref.getString(Globals.PREF_CUR_USER_KEY, "").toString()

        usersRef.child(username).get().addOnSuccessListener { snapshot ->
            usernameText.text = username
            if(snapshot.exists()) {
                passwordText.text = snapshot.child("password").value.toString()
            }
        }.addOnFailureListener {
            Toast.makeText(requireActivity(), "Failed to access database", Toast.LENGTH_SHORT).show()
        }

        logoutBtn.setOnClickListener() {
            currentUserPref.edit().putString(Globals.PREF_CUR_USER_KEY, "").commit()

            val intent = Intent(requireActivity(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish() // So users can't hit back to get to this
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}