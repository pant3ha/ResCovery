package com.example.rescovery.adapters

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rescovery.AppDatabase
import com.example.rescovery.Globals
import com.example.rescovery.R
import com.example.rescovery.RestaurantRepository
import com.example.rescovery.events.ViewEventActivity
import com.example.rescovery.ui.home.fragments.RestaurantViewModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EventsRecyclerAdapter(
    private val context: Context,
    private val restaurantViewModel: RestaurantViewModel,
    private val items: ArrayList<String>
) : RecyclerView.Adapter<EventsRecyclerAdapter.EventsViewHolder>() {

    // Firebase Database
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val eventsRef: DatabaseReference = database.getReference("events")
    private val usersRef: DatabaseReference = database.getReference("users")

    // Current user
    private val currentUserPref = context.getSharedPreferences(Globals.PREF_CUR_USER_NAME, MODE_PRIVATE)
    private val currentUser = currentUserPref.getString(Globals.PREF_CUR_USER_KEY, "").toString()

    // ViewHolder class
    class EventsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val restaurantText: TextView = view.findViewById(R.id.events_list_restaurant)
        val timeText: TextView = view.findViewById(R.id.events_list_time)
        val dateText: TextView = view.findViewById(R.id.events_list_date)
        val hostText: TextView = view.findViewById(R.id.events_list_host)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventsViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.listadapter_events, parent, false)

        return EventsViewHolder(view).apply {
            view.setOnClickListener {
                val intent = Intent(context, ViewEventActivity::class.java)
                intent.putExtra(Globals.EVENT_VIEW_ID, items[adapterPosition])
                context.startActivity(intent)
            }
        }
    }

    override fun onBindViewHolder(holder: EventsViewHolder, position: Int) {
        val eventKey = items[position]

        println("Adapter current event key: " + eventKey)

        // Get data
        eventsRef.child(eventKey).get().addOnSuccessListener { snapshot ->
            if(snapshot == null) {
                usersRef.child(currentUser).child("events").child("upcoming").child(eventKey).removeValue()
                usersRef.child(currentUser).child("events").child("invited").child(eventKey).removeValue()
                usersRef.child(currentUser).child("events").child("owned").child(eventKey).removeValue()

                items.removeAt(position)
                notifyItemRemoved(position)
                return@addOnSuccessListener
            }

            holder.restaurantText.text = snapshot.child("restaurantName").value.toString()

            // Time
            val datetime = Calendar.getInstance()
            datetime.timeInMillis = snapshot.child("dateTime").value.toString().toLong()
            if(datetime.timeInMillis < Calendar.getInstance().timeInMillis) {
                usersRef.child(currentUser).child("events").child("upcoming").child(eventKey).removeValue()
                usersRef.child(currentUser).child("events").child("invited").child(eventKey).removeValue()
                usersRef.child(currentUser).child("events").child("owned").child(eventKey).removeValue()

                items.removeAt(position)
                notifyItemRemoved(position)
                return@addOnSuccessListener
            }

            val timeFormat = SimpleDateFormat("hh:mma", Locale.getDefault())
            holder.timeText.text = timeFormat.format(datetime.time)

            val dateFormat = SimpleDateFormat("MMM dd, YYYY", Locale.getDefault())
            holder.dateText.text = dateFormat.format(datetime.time)

            holder.hostText.text = snapshot.child("hostName").value.toString()
        }

        // Example: You can fetch event details from Firebase or other sources
        holder.timeText.text = "12:00 PM" // Replace with actual data
        holder.dateText.text = "2024-12-01" // Replace with actual data
        holder.hostText.text = "Host Name" // Replace with actual data

        // TODO: Fetch event details using Firebase database and populate views
    }

    override fun getItemCount(): Int {
        return items.size
    }
}