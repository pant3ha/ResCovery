package com.example.rescovery

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.rescovery.ImageUtils.decode
import com.example.rescovery.data.User
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AccountSettingsActivity : AppCompatActivity() {

    private lateinit var closeBtn: ImageView
    private lateinit var logoutBtn: Button
    private lateinit var deleteBtn: Button
    private lateinit var saveBtn: Button
    private lateinit var editPictureText: TextView
    private lateinit var profileImageView: ImageView

    private lateinit var nameEdit: EditText
    private lateinit var usernameEdit: EditText
    private lateinit var bioEdit: EditText

    // Data
    private var selectedImageUri: Uri? = null
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->

        if (uri != null) {
            selectedImageUri = uri
            profileImageView.setImageURI(selectedImageUri)
        }
    }
    private var currentUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_settings)

        closeBtn = findViewById(R.id.profile_close_btn)
        logoutBtn = findViewById(R.id.profile_logout_btn)
        deleteBtn = findViewById(R.id.profile_delete_btn)
        saveBtn = findViewById(R.id.profile_save_btn)
        editPictureText = findViewById(R.id.edit_picture_text_btn)
        profileImageView = findViewById(R.id.profile_image)

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
            currentUser = snapshot.getValue(User::class.java)

            currentUser?.let {
                nameEdit.setText(it.fullName)
                usernameEdit.setText(it.username)
                bioEdit.setText(it.bio)

                //use ImageUtils to decode from string
                val decodedBitmap = decode(currentUser!!.profileImage ?: "")

                // Check if decoding was successful
                if (decodedBitmap != null) {
                    profileImageView.setImageBitmap(decodedBitmap)
                } else {
                    Glide.with(this)
                        .load(R.drawable.profile)
                        .into(profileImageView)
                }

            }
        }

        // Handle profile picture change
        editPictureText.setOnClickListener {
            selectImage()
        }

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

                // Convert the selected image to base64 and save
                selectedImageUri?.let { uri ->
                    val base64Image = ImageUtils.encode(this, uri)
                    if (base64Image != null) {
                        usersRef.child(username).child("profileImage").setValue(base64Image)
                    } else {
                        Toast.makeText(this, "Failed to save image!", Toast.LENGTH_SHORT).show()
                    }
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

    companion object {
        private const val REQUEST_CODE_READ_MEDIA_IMAGES = 1001
        private const val REQUEST_CODE_READ_EXTERNAL_STORAGE = 1002
    }

    private fun selectImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13 and above: Check READ_MEDIA_IMAGES permission
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_MEDIA_IMAGES
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                    REQUEST_CODE_READ_MEDIA_IMAGES
                )
            }
        } else {
            // Below Android 13: Check READ_EXTERNAL_STORAGE permission
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_CODE_READ_EXTERNAL_STORAGE
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            when (requestCode) {
                REQUEST_CODE_READ_MEDIA_IMAGES, REQUEST_CODE_READ_EXTERNAL_STORAGE -> {
                    pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }
            }
        } else {
            // Permission denied
            Toast.makeText(this, "Permission required to select an image.", Toast.LENGTH_SHORT).show()
        }
    }
}