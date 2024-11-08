package com.example.rescovery.profile_stuff

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.rescovery.Globals
import com.example.rescovery.MainActivity
import com.example.rescovery.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Get ui elements
        val usernameText: EditText = findViewById(R.id.login_username)
        val passwordText: EditText = findViewById(R.id.login_password)
        val submitBtn: Button = findViewById(R.id.login_submit_btn)

        val currentUserPref = getSharedPreferences(Globals.PREF_CUR_USER_NAME, MODE_PRIVATE)

        // Get the users section for the database
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val usersRef: DatabaseReference = database.getReference("users")

        submitBtn.setOnClickListener() {
            val enteredUsername = usernameText.text.toString().trim()
            val enteredPassword = passwordText.text.toString().trim()

            if (enteredUsername.isEmpty() || enteredPassword.isEmpty()) {
                Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            usersRef.child(enteredUsername).get().addOnSuccessListener { snapshot ->
                if(snapshot.exists()) {
                    val storedPassword = snapshot.child("password").value.toString()

                    if(enteredPassword == storedPassword) {
                        currentUserPref.edit().putString(Globals.PREF_CUR_USER_KEY, enteredUsername).apply()

                        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Username not found", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to access database", Toast.LENGTH_SHORT).show()
            }

        }
    }
}