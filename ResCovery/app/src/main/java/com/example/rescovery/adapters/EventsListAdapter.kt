package com.example.rescovery.adapters

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.rescovery.Globals
import com.example.rescovery.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class EventsListAdapter(
    private val context: Context,
    private val resource: Int,
    private val items: ArrayList<String>
) : ArrayAdapter<String>(context, resource, items) {

    // Database
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val eventsRef: DatabaseReference = database.getReference("events")

    // Current user
    private val currentUserPref = context.getSharedPreferences(Globals.PREF_CUR_USER_NAME, MODE_PRIVATE)
    private val currentUser = currentUserPref.getString(Globals.PREF_CUR_USER_KEY, "").toString()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(resource, parent, false)

        val eventKey = items[position]

        val restaurantText = view.findViewById<TextView>(R.id.events_list_restaurant)
        val timeText = view.findViewById<TextView>(R.id.events_list_time)
        val dateText = view.findViewById<TextView>(R.id.events_list_date)
        val hostText = view.findViewById<TextView>(R.id.events_list_host)

        // Get event details
        TODO("Finish")

        return view
    }
}