package com.example.rescovery

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AccountSettingsActivity : AppCompatActivity() {

    private lateinit var closeBtn: ImageView
    private lateinit var deleteBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_account_settings)

        closeBtn = findViewById(R.id.profile_close_btn)
        deleteBtn = findViewById(R.id.profile_delete_btn)

        closeBtn.setOnClickListener {
            finish()
        }

        deleteBtn.setOnClickListener {
            finish()
        }
    }
}