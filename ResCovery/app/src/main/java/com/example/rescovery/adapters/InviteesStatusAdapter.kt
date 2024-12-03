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
import com.example.rescovery.Restaurant
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class InviteesStatusAdapter(
    private val context: Context,
    private val resource: Int,
    private val items: ArrayList<Pair<String, String>>
) : ArrayAdapter<Pair<String, String>>(context, resource, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(resource, parent, false)

        view.findViewById<TextView>(R.id.event_invited_username).text = items[position].first
        view.findViewById<TextView>(R.id.event_invited_status).text = items[position].second

        return view
    }
}