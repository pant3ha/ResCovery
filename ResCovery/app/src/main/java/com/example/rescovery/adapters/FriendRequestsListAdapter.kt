package com.example.rescovery.adapters

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import com.example.rescovery.Globals
import com.example.rescovery.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class FriendRequestsListAdapter(
    private val context: Context,
    private val resource: Int,
    private val items: ArrayList<String>
    ) : ArrayAdapter<String>(context, resource, items) {

    // Database
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val usersRef: DatabaseReference = database.getReference("users")

    // Current user
    private val currentUserPref = context.getSharedPreferences(Globals.PREF_CUR_USER_NAME, MODE_PRIVATE)
    private val currentUser = currentUserPref.getString(Globals.PREF_CUR_USER_KEY, "").toString()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(context).inflate(resource, parent, false)

            val item = items[position]

            // Username
            val username = view.findViewById<TextView>(R.id.incoming_requests_username)
            username.text = item

            // Add to friends
            val addBtn = view.findViewById<Button>(R.id.incoming_requests_add)
            addBtn.setOnClickListener {
                // Remove from friendRequests
                usersRef.child(currentUser).child("friendRequests").child(item).removeValue()
                usersRef.child(item).child("friendRequests").child(currentUser).removeValue()

                // Add to friends list
                usersRef.child(currentUser).child("friends").child(item).setValue(true)
                usersRef.child(item).child("friends").child(currentUser).setValue(true)
            }

            val removeBtn = view.findViewById<Button>(R.id.incoming_requests_remove)
            removeBtn.setOnClickListener {
                // Remove from friendRequests
                usersRef.child(currentUser).child("friendRequests").child(item).removeValue()
                usersRef.child(item).child("friendRequests").child(currentUser).removeValue()
            }

            return view
        }
}