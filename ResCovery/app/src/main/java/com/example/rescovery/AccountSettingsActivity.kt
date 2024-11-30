package com.example.rescovery

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AccountSettingsActivity : AppCompatActivity() {

    private lateinit var closeBtn: ImageView
    private lateinit var logoutBtn: Button
    private lateinit var deleteBtn: Button
    private lateinit var saveBtn: Button

    private lateinit var nameEdit: EditText
    private lateinit var usernameEdit: EditText
    private lateinit var bioEdit: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_settings)

        closeBtn = findViewById(R.id.profile_close_btn)
        logoutBtn = findViewById(R.id.profile_logout_btn)
        deleteBtn = findViewById(R.id.profile_delete_btn)
        saveBtn = findViewById(R.id.profile_save_btn)

        nameEdit = findViewById(R.id.profile_edit_name)
        usernameEdit = findViewById(R.id.profile_edit_username)
        bioEdit = findViewById(R.id.profile_edit_bio)

        // Get info from database
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val usersRef: DatabaseReference = database.getReference("users")

        // Get current user
        val currentUserPref = getSharedPreferences(Globals.PREF_CUR_USER_NAME, MODE_PRIVATE)
        val username = currentUserPref.getString(Globals.PREF_CUR_USER_KEY, "").toString()

        // Get name and bio
        var fullName = ""
        var newUsername = ""
        var bio = ""

        usersRef.child(username).get().addOnSuccessListener { snapshot ->
            fullName = snapshot.child("fullName").value.toString()
            nameEdit.setText(fullName)
            if(snapshot.child("bio").exists()) {
                bio = snapshot.child("bio").value.toString()
                bioEdit.setText(bio)
            }
        }
        usernameEdit.setText(username)

        saveBtn.setOnClickListener {
            fullName = nameEdit.text.toString().trim()
            newUsername = usernameEdit.text.toString().trim()
            bio = bioEdit.text.toString().trim()

            if(fullName.isEmpty() || newUsername.isEmpty()) {
                Toast.makeText(this, "Name cannot be empty!", Toast.LENGTH_SHORT).show()
            } else {
                // Save full name and bio
                usersRef.child(username).child("fullName").setValue(fullName)
                if(bio.isEmpty()) {
                    usersRef.child(username).child("bio").removeValue()
                } else {
                    usersRef.child(username).child("bio").setValue(bio)
                }

                Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show()
            }
        }

        closeBtn.setOnClickListener {
            finish()
        }

        logoutBtn.setOnClickListener {
            currentUserPref.edit().putString(Globals.PREF_CUR_USER_KEY, "").commit()

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // So users can't hit back to get to this
        }

        deleteBtn.setOnClickListener {
            usersRef.get().addOnSuccessListener { snapshot ->
                for(user in snapshot.children) {
                    user.key?.let {
                        usersRef.child(it).child("friends").child(username).removeValue()
                        usersRef.child(it).child("friendRequests").child(username).removeValue()
                    }
                }
            }

            usersRef.child(username).removeValue()
            currentUserPref.edit().putString(Globals.PREF_CUR_USER_KEY, "").commit()

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            Toast.makeText(this, "Account Deleted", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}