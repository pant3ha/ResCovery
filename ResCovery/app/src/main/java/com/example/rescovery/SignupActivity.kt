package com.example.rescovery

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.rescovery.data.User
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignupActivity : AppCompatActivity() {

    private lateinit var signupBtn: Button
    private lateinit var loginBtn: TextView
    private lateinit var nameText: EditText
    private lateinit var usernameText: EditText
    private lateinit var emailText: EditText
    private lateinit var passwordText: EditText

    // Database
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val usersRef: DatabaseReference = database.getReference("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signup)

        signupBtn = findViewById(R.id.signup_btn)
        loginBtn = findViewById(R.id.login_text_btn)
        nameText = findViewById(R.id.signup_name)
        usernameText = findViewById(R.id.signup_username)
        emailText = findViewById(R.id.signup_email)
        passwordText = findViewById(R.id.signup_password)


        signupBtn.setOnClickListener {
            registerUser()
        }
        loginBtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        // text logic
        nameText.addTextChangedListener( object: TextWatcher {
            // Unneeded
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                onEdit(nameText)
            }
        })

        usernameText.addTextChangedListener( object: TextWatcher {
            // Unneeded
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                onEdit(usernameText)
            }
        })

        emailText.addTextChangedListener( object: TextWatcher {
            // Unneeded
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                onEdit(emailText)
            }
        })

        passwordText.addTextChangedListener( object: TextWatcher {
            // Unneeded
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                onEdit(passwordText)
            }
        })
    }

    private fun registerUser() {
        // Get values
        val enteredName = nameText.text.toString().trim()
        val enteredUsername = usernameText.text.toString().trim()
        val enteredEmail = emailText.text.toString().trim()
        val enteredPassword = passwordText.text.toString().trim()

        val texts = arrayOf(enteredName, enteredUsername, enteredEmail, enteredPassword)
        val fields = arrayOf(nameText, usernameText, emailText, passwordText)

        // Check if fields are empty
        var toastRunning = false
        for (i in texts.indices) {
            if(texts[i].isEmpty()) {
                if(!toastRunning) {
                    Toast.makeText(this, "Cannot be empty!", Toast.LENGTH_SHORT).show()
                    toastRunning = true
                }

                val bgColour = ContextCompat.getColor(this, R.color.red)
                fields[i].backgroundTintList = ColorStateList.valueOf(bgColour)
            }
        }
        if (toastRunning) return

        // Check if username already exists
        usersRef.child(enteredUsername).get().addOnSuccessListener { snapshot ->
            if(snapshot.exists()) {
                val bgColour = ContextCompat.getColor(this, R.color.red)
                usernameText.backgroundTintList = ColorStateList.valueOf(bgColour)

                Toast.makeText(this, "Username taken!", Toast.LENGTH_SHORT).show()
            } else {
                // Register user
                val defaultProfileImage = ImageUtils.encodeDrawable(this, R.drawable.profile)
                val user = User(enteredName, enteredUsername, enteredEmail, enteredPassword, "",defaultProfileImage)
                usersRef.child(enteredUsername).setValue(user)

                Toast.makeText(this, "Registered", Toast.LENGTH_SHORT).show()

                // Log in
                val currentUserPref = getSharedPreferences(Globals.PREF_CUR_USER_NAME, MODE_PRIVATE)
                currentUserPref.edit().putString(Globals.PREF_CUR_USER_KEY, enteredUsername).apply()

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Database Failure", Toast.LENGTH_SHORT).show()
        }

    }

    private fun onEdit(editText: EditText) {
        val bgColour = ContextCompat.getColor(this, R.color.blue)
        editText.backgroundTintList = ColorStateList.valueOf(bgColour)
    }

    private data class EnteredUser(
        val email: String,
        val fullName: String,
        val password: String)
}