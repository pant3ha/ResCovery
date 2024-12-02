package com.example.rescovery.ui.home.fragments.map

import android.app.Application
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rescovery.RestaurantRepository
import android.Manifest
import android.media.MicrophoneInfo.Coordinate3F
import androidx.lifecycle.viewModelScope
import com.example.rescovery.Restaurant
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

class MapViewModel(private val application: Application, private val repository: RestaurantRepository) : AndroidViewModel(application), LocationListener {
    private val TAG = "MapViewModel"
    private val _currentLocation = MutableLiveData<Location>()
    val currentLocation : LiveData<Location> get() = _currentLocation
    private val locationManager : LocationManager = application.getSystemService(LocationManager::class.java)
    private val _restaurants = MutableLiveData<List<Restaurant>>()
    val restaurants: LiveData<List<Restaurant>>get() = _restaurants

    init {
        //load the static restaurants
        loadRestaurants()
        //check permissions
        if(ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)
        } else { Log.d(TAG, "Location permission denied in ViewModel")
        Log.d(TAG, "MapViewModel opened") }
    }

    //load all restaurants with the getAllRestaurants query from repository
    private fun loadRestaurants() {
        viewModelScope.launch {
            repository.getAllRestaurants().collect { restaurantList ->
                _restaurants.value = restaurantList
                Log.d(TAG, "loaded  restaurants")
            }
        }
    }

    //translates string to lat an lng
    fun getCoordinates(coordinates: String): LatLng {
        val (lat, lng) = coordinates.split(",").map { it.trim().toDouble()}
        return LatLng(lat,lng)
    }

    //users location is changed
    override fun onLocationChanged(location: Location) {
        _currentLocation.value = location
    }

    override fun onCleared() {
        super.onCleared()
        locationManager.removeUpdates(this)
    }

    class MapViewModelFactory(private val application: Application, val repository: RestaurantRepository) : ViewModelProvider.Factory {
        override fun <T: ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(MapViewModel::class.java)) {
                return MapViewModel(application, repository) as T
            }
            throw IllegalArgumentException("Unknown viewmodel class")
        }
    }


}