package com.example.rescovery.ui.home.fragments.map

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.rescovery.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import android.location.Location
import androidx.lifecycle.ViewModelProvider
import com.example.rescovery.AppDatabase
import com.example.rescovery.Restaurant
import com.example.rescovery.RestaurantDatabaseDao
import com.example.rescovery.RestaurantRepository
import com.example.rescovery.ui.home.fragments.RestaurantFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment(), OnMapReadyCallback {
    private val LOCATION_PERMISSION_REQUEST_CODE = 0
    private lateinit var mapViewModel: MapViewModel
    private val TAG = "MapFragment"
    private var mapCentred = false
    private lateinit var googleMap: GoogleMap
    private var currentMarker: Marker? = null



    companion object {
        fun newInstance() = MapFragment()
    }

    private val viewModel: MapViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_map,container, false)
        val fragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        fragment.getMapAsync(this)

        val repository = RestaurantRepository(AppDatabase.getInstance(requireActivity().application).restaurantDatabaseDao)
        val factory = MapViewModel.MapViewModelFactory(requireActivity().application, repository)
        mapViewModel = ViewModelProvider(this, factory).get(MapViewModel::class.java)

        return view
    }


    override fun onMapReady(map: GoogleMap) {
        Log.d(TAG, "Map is ready")
        googleMap = map
        if (hasLocationPermission()) {
            showLocationOnMap()
        } else {
            requestLocationPermission()
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions ( arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            showLocationOnMap()
        } else {
            Log.d(TAG, "Location permission denied")
        }
    }

    private fun showLocationOnMap() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.isMyLocationEnabled = true
            observeTracking()
        }
        observeRestaurants()
        setupMarkerClickListener()
    }

    private fun observeTracking() {
        mapViewModel.currentLocation.observe(viewLifecycleOwner) { location ->
            Log.d(TAG, "current location updated: $location")
            updateCurrentMarker(location)
            val currentLatLng = LatLng(location.latitude, location.longitude)
            if (!mapCentred) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                mapCentred = true
                Log.d(TAG, "map zoomed")
            }
        }
    }

    private fun setupMarkerClickListener() {
        googleMap.setOnMarkerClickListener { marker ->
            val restaurant = marker.tag as? Restaurant
            restaurant?.let {
                openRestaurantFragment(it)
            }
            true
        }
    }

    private fun openRestaurantFragment(restaurant: Restaurant) {
        val fragment = RestaurantFragment.newInstance(restaurant)
        parentFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit()
    }

    private fun observeRestaurants() {
        mapViewModel.restaurants.observe(viewLifecycleOwner) { restaurants ->
            restaurants.forEach { restaurant ->
                val pos = mapViewModel.getCoordinates(restaurant.coordinates)
                val marker = googleMap.addMarker(MarkerOptions().position(pos).title(restaurant.restaurantName).snippet(restaurant.description))
                marker?.tag = restaurant
            }
        }
    }

    private fun updateCurrentMarker(location: Location) {
        val currentLatLng = LatLng(location.latitude, location.longitude)
        currentMarker?.remove()
        currentMarker = googleMap.addMarker(MarkerOptions().position(currentLatLng).title("Current Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)))
        Log.d(TAG, "Current Marker updated to: $currentLatLng")
    }
}