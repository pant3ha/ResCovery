package com.example.rescovery.events

import android.app.ComponentCaller
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.rescovery.Globals
import com.example.rescovery.R
import com.example.rescovery.Restaurant
import com.example.rescovery.adapters.ShowInviteesAdapter
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CreateEventActivity : AppCompatActivity() {

    // Database
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val usersRef: DatabaseReference = database.getReference("users")
    private val eventsRef: DatabaseReference = database.getReference("events")

    // Current user
    private var currentUser = ""

    // Other Stuff
    private var eventKey = ""
    private var restaurant = Restaurant()
    private var time = Calendar.getInstance()
    private var invitees = ArrayList<String>()

    // Get results from child activities
    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val intent = result.data
            if (intent != null) {
                // Get restaurant
                val selectedRestaurant = intent.getParcelableExtra<Restaurant>(Globals.EVENT_RESTAURANT_KEY)
                if (selectedRestaurant != null) {
                    // Update the UI with the selected restaurant
                    println(selectedRestaurant.restaurantName)
                    findViewById<TextView>(R.id.eventedit_restaurant_text).text = selectedRestaurant.restaurantName
                    restaurant = selectedRestaurant
                }

                val selectedInvitees = intent.getStringArrayListExtra(Globals.EVENT_INVITEES_KEY)
                if(selectedInvitees != null) {
                    invitees.clear()
                    invitees.addAll(selectedInvitees)
                    val inviteesListView = findViewById<ListView>(R.id.eventedit_list)
                    val inviteesAdapter = ShowInviteesAdapter(this, R.layout.listadapter_restaurant, selectedInvitees)
                    inviteesListView.adapter = inviteesAdapter
                    inviteesAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)

        val currentUserPref = getSharedPreferences(Globals.PREF_CUR_USER_NAME, MODE_PRIVATE)
        currentUser = currentUserPref.getString(Globals.PREF_CUR_USER_KEY, "").toString()

        findViewById<TextView>(R.id.eventedit_host_text).text = currentUser

        val tempEventKey = intent.getStringExtra(Globals.EVENT_ID_KEY)
        if(tempEventKey.isNullOrEmpty()) {
            eventKey = eventsRef.push().key.toString()
            if(eventKey.isEmpty()) {
                println("Database error!")
                finish()
            }
        }

        // Edit Restaurant
        findViewById<Button>(R.id.eventedit_restaurant_btn).setOnClickListener {
            startForResult.launch(Intent(this, ChooseRestaurantActivity::class.java))
        }

        // Edit Time
        findViewById<Button>(R.id.eventedit_time_btn).setOnClickListener {
            // Timepicker
            val timePickerDialog = TimePickerDialog(
                this,
                TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                    // Set the selected time in the Calendar instance
                    time.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    time.set(Calendar.MINUTE, minute)

                    // Format the time to display in "hh:mmAM/PM"
                    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                    val formattedTime = timeFormat.format(time.time)

                    // Format the date as "MMM dd, yyyy"
                    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                    val formattedDate = dateFormat.format(time.time)

                    // Combine date and time in the format "hh:mmAM MMM dd, yyyy"
                    val formattedDateTime = "$formattedTime $formattedDate"

                    // Update the UI with selected date and time
                    findViewById<TextView>(R.id.eventedit_time_text).text = formattedDateTime
                },
                time.get(Calendar.HOUR_OF_DAY),  // Default hour
                time.get(Calendar.MINUTE),      // Default minute
                false
            )

            // Datepicker
            val datePickerDialog = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { _, year, month, day ->
                    time.set(Calendar.YEAR, year)
                    time.set(Calendar.MONTH, month)
                    time.set(Calendar.DAY_OF_MONTH, day)

                    timePickerDialog.show()
                },
                time.get(Calendar.YEAR),
                time.get(Calendar.MONTH),
                time.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }

        // Edit Invitees
        findViewById<Button>(R.id.eventedit_invitees_btn).setOnClickListener {
            startForResult.launch(Intent(this, ChooseInviteesActivity::class.java))
        }

        // Save
        findViewById<Button>(R.id.eventedit_create_btn).setOnClickListener {
            save()
        }

        // Cancel
        findViewById<Button>(R.id.eventedit_cancel_btn).setOnClickListener {
            finish()
        }
    }

    fun save() {
        if(restaurant.id <= 0) {
            Toast.makeText(this, "Restaurant must be inputted", Toast.LENGTH_SHORT).show()
            return
        }

        if(time.timeInMillis < Calendar.getInstance().timeInMillis) {
            Toast.makeText(this, "Cannot set time that has already passed", Toast.LENGTH_SHORT).show()
            return
        }

        val event = Event(restaurant.id, restaurant.restaurantName, currentUser, time.timeInMillis)
        eventsRef.child(eventKey).setValue(event)
        usersRef.child(currentUser).child("events").child("owned").child(eventKey).setValue(event.dateTime)
        usersRef.child(currentUser).child("events").child("upcoming").child(eventKey).setValue(event.dateTime)
        for(invitee in invitees) {
            eventsRef.child(eventKey).child("invitees").child(invitee).setValue("pending")
            usersRef.child(invitee).child("events").child("invited").child(eventKey).setValue(event.dateTime)
        }

        finish()
    }

}