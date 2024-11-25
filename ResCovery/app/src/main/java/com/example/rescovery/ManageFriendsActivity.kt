package com.example.rescovery

import android.os.Bundle
import android.renderscript.Sampler.Value
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.rescovery.adapters.FriendRequestsListAdapter
import com.example.rescovery.adapters.FriendsListAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ManageFriendsActivity : AppCompatActivity() {

    // User
    private lateinit var currentUser : String

    // Database
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val usersRef: DatabaseReference = database.getReference("users")

    // Other vars
    private val friends: ArrayList<String> = arrayListOf()

    private lateinit var addText : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_friends)

        // Current user
        val currentUserPref = getSharedPreferences(Globals.PREF_CUR_USER_NAME, MODE_PRIVATE)
        currentUser = currentUserPref.getString(Globals.PREF_CUR_USER_KEY, "").toString()

        // Database
        val friendsRef: DatabaseReference = usersRef.child(currentUser).child("friends")

        // Back button
        val backBtn = findViewById<Button>(R.id.manage_friends_back)
        backBtn.setOnClickListener { finish() }

        // Add a new friend
        addText = findViewById<EditText>(R.id.manage_friends_add_friend)
        val addBtn = findViewById<Button>(R.id.manage_friends_add_btn)

        addBtn.setOnClickListener {
            val friendUser = addText.text.toString().trim()
            addFriend(friendUser)
        }

        // Show friends list
        val friendsListAdapter = FriendsListAdapter(
            this,
            R.layout.listadapter_friends,
            friends)
        val friendsList = findViewById<ListView>(R.id.manage_friends_lst)
        friendsList.adapter = friendsListAdapter

        val friendListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                friends.clear()
                for(child in snapshot.children) {
                    child.key?.let { friends.add(it) }
                }
                friendsListAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}

        }
        friendsRef.addValueEventListener(friendListener)
    }

    private fun addFriend(friendUser : String) {
        if(friendUser == "") {
            Toast.makeText(this,"Cannot be empty!", Toast.LENGTH_SHORT).show()
            return
        }

        if(friendUser == currentUser) {
            Toast.makeText(this,"You cannot add yourself!", Toast.LENGTH_SHORT).show()
        } else {
            // Check if user exists in database
            usersRef.child(friendUser).get().addOnSuccessListener { snapshot ->
                if(!snapshot.exists()) {
                    Toast.makeText(this,"$friendUser does not exist!", Toast.LENGTH_SHORT).show()
                } else if (snapshot.child("friends").child(currentUser).exists()) {
                    // Check if already friends
                    Toast.makeText(this,"Already friends!", Toast.LENGTH_SHORT).show()
                } else {
                    usersRef.child(friendUser).child("friendRequests").child(currentUser).setValue(true)
                    Toast.makeText(this,"Request sent!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        addText.setText("")
    }
}