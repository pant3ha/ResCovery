package com.example.rescovery

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AccountSettingsActivity : AppCompatActivity() {

    private lateinit var closeBtn: ImageView
    private lateinit var logoutBtn: Button
    private lateinit var deleteBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_account_settings)

        closeBtn = findViewById(R.id.profile_close_btn)
        logoutBtn = findViewById(R.id.profile_logout_btn)
        deleteBtn = findViewById(R.id.profile_delete_btn)

        closeBtn.setOnClickListener {
            finish()
        }

        logoutBtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            Toast.makeText(this, "Logging Out", Toast.LENGTH_SHORT).show()
            finish()
        }

        deleteBtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            Toast.makeText(this, "Account Deleted", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}