package com.example.rescovery

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Build
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.example.rescovery.data.Post
import kotlinx.coroutines.launch

class AddPostActivity : AppCompatActivity() {

    // Database
    private var postsRef: DatabaseReference? = null

    // UI Elements
    private lateinit var restaurantSpinner: Spinner
    private lateinit var ratingBar: RatingBar
    private lateinit var review: EditText
    private lateinit var imageButton: LinearLayout
    private lateinit var imageView: ImageView
    private lateinit var closeBtn: ImageView
    private lateinit var submitBtn: Button

    // Data
    private var imageUri: Uri? = null
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->

        if (uri != null) {
            imageUri = uri
            imageView.setImageURI(imageUri)
            imageButton.visibility = View.GONE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_post)
        postsRef = FirebaseDatabase.getInstance().getReference("posts")

        // Get UI elements
        restaurantSpinner = findViewById(R.id.add_post_spinner)
        ratingBar = findViewById(R.id.add_post_ratingBar)
        review = findViewById(R.id.add_post_review)
        imageButton = findViewById(R.id.add_post_image_btn)
        imageView = findViewById(R.id.add_post_imageView)
        closeBtn = findViewById(R.id.add_post_close_btn)
        submitBtn = findViewById(R.id.add_post_submit_btn)

        ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            if (rating < 1) {
                ratingBar.rating = 1f  // Set rating to 1 if it's less than 1
            }
        }

        imageButton.setOnClickListener {
            selectImage()
        }

        closeBtn.setOnClickListener {
            finish()
        }

        submitBtn.setOnClickListener {
            uploadPost()
        }

    }

    private fun uploadPost() {
        lifecycleScope.launch {
            // Get values
            val selectedRestaurant = restaurantSpinner.selectedItemPosition
            val enteredRating = ratingBar.rating
            val enteredReview = review.text.toString().trim()


            // Check for image and restaurant, rating
            when {
                selectedRestaurant == 0 -> Toast.makeText(this@AddPostActivity, "Select restaurant", Toast.LENGTH_SHORT).show()
                enteredRating < 1 -> Toast.makeText(this@AddPostActivity, "Minimum rating is 1 star", Toast.LENGTH_SHORT).show()
                imageUri == null -> Toast.makeText(this@AddPostActivity, "Select image!", Toast.LENGTH_SHORT).show()

                else -> {
                    // Convert image to base64
                    val base64Image = ImageUtils.encode(this@AddPostActivity, imageUri!!)
                    if (base64Image == null) {
                        Toast.makeText(this@AddPostActivity, "Failed to process image", Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                    // Get current user
                    val currentUserPref = getSharedPreferences(Globals.PREF_CUR_USER_NAME, MODE_PRIVATE)
                    val publisherId = currentUserPref.getString(Globals.PREF_CUR_USER_KEY, "")
                    if (publisherId == null) {
                        Toast.makeText(this@AddPostActivity, "Failed to identify user", Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                    // Upload Post
                    val postId = postsRef!!.push().key
                    if (postId == null) {
                        Toast.makeText(this@AddPostActivity, "Couldn't get push key for posts", Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                    val post = Post(publisherId, selectedRestaurant, enteredRating, enteredReview, base64Image)
                    val postValues = post.toMap()

                    postsRef!!.child(postId).updateChildren(postValues)
                        .addOnSuccessListener {
                            Toast.makeText(this@AddPostActivity, "Post Submitted", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this@AddPostActivity, "Failed to Submit post", Toast.LENGTH_SHORT).show()
                        }
                }
            }
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