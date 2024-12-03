package com.example.rescovery.events

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.rescovery.Globals
import com.example.rescovery.R
import com.example.rescovery.adapters.InviteesStatusAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ViewEventActivity : AppCompatActivity() {
    private lateinit var eventKey: String

    // Database
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val usersRef: DatabaseReference = database.getReference("users")
    private val eventsRef: DatabaseReference = database.getReference("events")

    // Current user
    private var currentUser = ""

    // Other vars
    private var invitees = arrayListOf<Pair<String, String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_event)

        intent.getStringExtra(Globals.EVENT_VIEW_ID)?.let {
            eventKey = it
        }

        val currentUserPref = getSharedPreferences(Globals.PREF_CUR_USER_NAME, MODE_PRIVATE)
        currentUser = currentUserPref.getString(Globals.PREF_CUR_USER_KEY, "").toString()

        findViewById<Button>(R.id.eventview_back).setOnClickListener { finish() }

        val restaurantNameText = findViewById<TextView>(R.id.eventview_restaurant_text)
        val hostNameText = findViewById<TextView>(R.id.eventview_host_text)
        val timeText = findViewById<TextView>(R.id.eventview_time_text)
        val inviteesListView = findViewById<ListView>(R.id.eventview_list)

        var time = Calendar.getInstance()

        // Get event
        println(eventKey)
        eventsRef.child(eventKey).get().addOnSuccessListener { snapshot ->
            snapshot?.let {
                // Set mode
                if(snapshot.child("invitees").child(currentUser).value.toString() == "pending")
                    inviteMode()
                else
                    viewMode()

                restaurantNameText.text = snapshot.child("restaurantName").value.toString()
                hostNameText.text = snapshot.child("hostName").value.toString()

                time = Calendar.getInstance()
                snapshot.child("dateTime").getValue(Long::class.java)?.let {
                    time.timeInMillis = it
                }
                // Format the time to display in "hh:mmAM/PM"
                val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                val formattedTime = timeFormat.format(time.time)

                // Format the date as "MMM dd, yyyy"
                val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                val formattedDate = dateFormat.format(time.time)

                // Combine date and time in the format "hh:mmAM MMM dd, yyyy"
                timeText.text = "$formattedTime $formattedDate"
            }
        }

        // Manage invitees
        val inviteeAdapter = InviteesStatusAdapter(
            this,
            R.layout.listadapter_event_invited,
            invitees)
        inviteesListView.adapter = inviteeAdapter

        val inviteeListener = object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                invitees.clear()
                for(child in snapshot.children)
                    invitees.add(Pair(child.key.toString(), child.value.toString()))
                inviteeAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                println(error)
            }
        }
        eventsRef.child(eventKey).child("invitees").addValueEventListener(inviteeListener)

        // Accept or Deny invite
        findViewById<Button>(R.id.eventview_accept_btn).setOnClickListener {
            viewMode()

            eventsRef.child(eventKey).child("invitees").child(currentUser).setValue("Accepted")

            usersRef.child(currentUser).child("events").child("upcoming").child(eventKey).setValue(time.timeInMillis)
            usersRef.child(currentUser).child("events").child("invited").child(eventKey).removeValue()

        }

        findViewById<Button>(R.id.eventview_deny_btn).setOnClickListener {
            viewMode()

            eventsRef.child(eventKey).child("invitees").child(currentUser).setValue("Declined")

            usersRef.child(currentUser).child("events").child("invited").child(eventKey).removeValue()
        }
    }

    fun inviteMode() {
        findViewById<LinearLayout>(R.id.eventview_btns).visibility = View.VISIBLE
    }

    fun viewMode() {
        findViewById<LinearLayout>(R.id.eventview_btns).visibility = View.GONE
    }
}