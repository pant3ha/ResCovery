package com.example.rescovery.events

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.rescovery.Globals
import com.example.rescovery.R
import com.example.rescovery.adapters.ShowInviteesAdapter
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.ArrayList

class ChooseInviteesActivity : AppCompatActivity() {

    private lateinit var inviteeListView: ListView
    private lateinit var friendListView: ListView

    val inviteeList: ArrayList<String> = ArrayList()
    val friendList: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_invitees)

        // Current user
        val currentUserPref = getSharedPreferences(Globals.PREF_CUR_USER_NAME, MODE_PRIVATE)
        val currentUser = currentUserPref.getString(Globals.PREF_CUR_USER_KEY, "").toString()

        // Database
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val usersRef: DatabaseReference = database.getReference("users")
        val friendsRef: DatabaseReference = usersRef.child(currentUser).child("friends")

        inviteeListView = findViewById(R.id.choose_invitees_invitees)
        friendListView = findViewById(R.id.choose_invitees_friends)

        // Create array adapters
        val inviteeAdapter = ShowInviteesAdapter(this, R.layout.listadapter_restaurant, inviteeList)
        val friendAdapter = ShowInviteesAdapter(this, R.layout.listadapter_restaurant, friendList)

        inviteeListView.adapter = inviteeAdapter
        friendListView.adapter = friendAdapter

        // Get friends
        friendsRef.get().addOnSuccessListener { snapshot ->
            for(friend in snapshot.children) {
                friend.key?.let { friendList.add(it) }
            }
            friendAdapter.notifyDataSetChanged()
        }.addOnFailureListener{
            Toast.makeText(this, "Failed to access database", Toast.LENGTH_SHORT).show()
        }

        // Set invitee list
        friendListView.setOnItemClickListener { _, _, pos, _ ->
            if(!inviteeList.contains(friendList[pos])) {
                inviteeList.add(friendList[pos])
            }
            inviteeAdapter.notifyDataSetChanged()
        }

        inviteeListView.setOnItemClickListener {_, _, pos, _ ->
            inviteeList.removeAt(pos)
            inviteeAdapter.notifyDataSetChanged()
        }

        findViewById<Button>(R.id.choose_invitees_done).setOnClickListener {
            val resultIntent = Intent().apply {
                putExtra(Globals.EVENT_INVITEES_KEY, inviteeList)
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }
}