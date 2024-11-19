package com.example.rescovery

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {

    private lateinit var loginBtn: Button
    private lateinit var signupBtn: TextView

    private lateinit var usernameText: EditText
    private lateinit var passwordText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        loginBtn = findViewById(R.id.login_btn)
        signupBtn = findViewById(R.id.signup_text_btn)

        usernameText = findViewById(R.id.login_username)
        passwordText = findViewById(R.id.login_password)

        // Send user to mainActivity if already logged in
        val currentUserPref = getSharedPreferences(Globals.PREF_CUR_USER_NAME, MODE_PRIVATE)
        val userid = currentUserPref.getString(Globals.PREF_CUR_USER_KEY, "")
        if(userid != ""){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // so users can't go back to this activity after logging in
        }

        loginBtn.setOnClickListener {
            checkUser()
        }

        signupBtn.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
    }

    private fun checkUser() {
        val currentUserPref = getSharedPreferences(Globals.PREF_CUR_USER_NAME, MODE_PRIVATE)

        // Get the users section for the database
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val usersRef: DatabaseReference = database.getReference("users")

        val enteredUsername = usernameText.text.toString().trim()
        val enteredPassword = passwordText.text.toString().trim()

        if (enteredUsername.isEmpty() || enteredPassword.isEmpty()) {
            Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show()
            return
        }

        usersRef.child(enteredUsername).get().addOnSuccessListener { snapshot ->
            if(snapshot.exists()) {
                val storedPassword = snapshot.child("password").value.toString()

                if(enteredPassword == storedPassword) {
                    currentUserPref.edit().putString(Globals.PREF_CUR_USER_KEY, enteredUsername).apply()

                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
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