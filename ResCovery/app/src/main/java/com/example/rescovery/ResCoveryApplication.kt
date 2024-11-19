package com.example.rescovery

import android.app.Application
import com.google.firebase.FirebaseApp



class ResCoveryApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize firebase
        FirebaseApp.initializeApp(this)
    }
}
