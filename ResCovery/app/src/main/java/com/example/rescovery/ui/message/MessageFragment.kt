package com.example.rescovery.ui.message

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rescovery.AppDatabase
import com.example.rescovery.Globals
import com.example.rescovery.ManageFriendsActivity
import com.example.rescovery.R
import com.example.rescovery.adapters.EventsRecyclerAdapter
import com.example.rescovery.adapters.FriendRequestsListAdapter
import com.example.rescovery.databinding.FragmentMessageBinding
import com.example.rescovery.events.CreateEventActivity
import com.example.rescovery.events.ViewEventActivity
import com.example.rescovery.ui.home.fragments.RestaurantViewModel
import com.example.rescovery.ui.home.fragments.RestaurantViewModelFactory
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MessageFragment : Fragment() {

    private var _binding: FragmentMessageBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // Current user
    private lateinit var currentUserPref : SharedPreferences
    private lateinit var username : String

    // Database
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val usersRef: DatabaseReference = database.getReference("users")
    private val eventsRef: DatabaseReference = database.getReference("events")
    private lateinit var friendsRef: DatabaseReference

    // Other vars
    private var requests : ArrayList<String> = arrayListOf()

    private var upcomingEvents: ArrayList<String> = arrayListOf()
    private var inviteEvents: ArrayList<String> = arrayListOf()
    private var ownedEvents: ArrayList<String> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val messageViewModel =
            ViewModelProvider(this).get(MessageViewModel::class.java)

        _binding = FragmentMessageBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Set some of the text to invisible
        binding.textView3.visibility = View.INVISIBLE

        // Current user
        currentUserPref = requireActivity().getSharedPreferences(Globals.PREF_CUR_USER_NAME, MODE_PRIVATE)
        username = currentUserPref.getString(Globals.PREF_CUR_USER_KEY, "").toString()

        // Database
        friendsRef = usersRef.child(username).child("friendRequests")

        // Manage friends
        binding.eventsFriendsbtn.setOnClickListener {
            val intent = Intent(requireActivity(), ManageFriendsActivity::class.java)
            startActivity(intent)
        }

        // Requests array adapter
        val friendRequestsListAdapter = FriendRequestsListAdapter(
            requireActivity(),
            R.layout.listadapter_incoming_requests,
            requests)
        binding.eventsFriendrequestslst.adapter = friendRequestsListAdapter

        // Watch for changes in database
        val friendListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                println(snapshot.toString())
                requests.clear()
                for(child in snapshot.children) {
                    child.key?.let {requests.add(it)}
                }
                if (requests.isEmpty()) {
                    binding.eventsFriendrequestslst.visibility = View.GONE
                    binding.textView3.visibility = View.VISIBLE
                } else {
                    binding.eventsFriendrequestslst.visibility = View.VISIBLE
                    binding.textView3.visibility = View.GONE
                }

                friendRequestsListAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        }
        friendsRef.addValueEventListener(friendListener)

        // Create Event
        binding.eventsEventbtn.setOnClickListener {
            val intent = Intent(requireActivity(), CreateEventActivity::class.java)
            startActivity(intent)
        }

        // Event list adapters
        //set up databases
        val restaurantDao = AppDatabase.getInstance(requireContext()).restaurantDatabaseDao
        val restaurantViewModel = ViewModelProvider(this, RestaurantViewModelFactory(restaurantDao, FirebaseDatabase.getInstance())
        )[RestaurantViewModel::class.java]

        val upcomingEventsAdapter = EventsRecyclerAdapter(
            requireActivity(),
            restaurantViewModel,
            upcomingEvents
        )
        binding.messageFragmentUpcomingEventsLs.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.messageFragmentUpcomingEventsLs.adapter = upcomingEventsAdapter

        val inviteEventsAdapter = EventsRecyclerAdapter(
            requireActivity(),
            restaurantViewModel,
            inviteEvents
        )
        binding.messageFragmentInviteEventsLs.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.messageFragmentInviteEventsLs.adapter = inviteEventsAdapter

        val ownedEventsAdapter = EventsRecyclerAdapter(
            requireActivity(),
            restaurantViewModel,
            ownedEvents
        )
        binding.messageFragmentOwnedEventsLs.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.messageFragmentOwnedEventsLs.adapter = ownedEventsAdapter


        // Get events data
        val eventsListener = object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var ls = arrayListOf<String>()
                for(child in snapshot.children) {
                    child.key?.let { ls.add(it) }
                }
                if(ls.isNotEmpty()) {
                    ls = ArrayList(ls.sortedBy {
                        snapshot.child(it).getValue(Long::class.java)
                    })
                }
                snapshot.key?.let{
                    println(snapshot.key)
                    when(it) {
                        "upcoming" -> {
                            upcomingEvents.clear()
                            upcomingEvents.addAll(ls)
                            println(upcomingEvents)
                            upcomingEventsAdapter.notifyDataSetChanged()
                        }
                        "invited" -> {
                            inviteEvents.clear()
                            inviteEvents.addAll(ls)
                            inviteEventsAdapter.notifyDataSetChanged()
                        }
                        "owned" -> {
                            ownedEvents.clear()
                            ownedEvents.addAll(ls)
                            ownedEventsAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println(error)
            }
        }
        usersRef.child(username).child("events").child("upcoming").addValueEventListener(eventsListener)
        usersRef.child(username).child("events").child("invited").addValueEventListener(eventsListener)
        usersRef.child(username).child("events").child("owned").addValueEventListener(eventsListener)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}