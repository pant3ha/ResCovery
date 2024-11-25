package com.example.rescovery

import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.text.Layout
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
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

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
    private var myUrl = ""
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
        // Get values
        val selectedRestaurant = restaurantSpinner.selectedItemPosition
        val enteredRating = ratingBar.rating
        val enteredReview = review.text.toString().trim()


        // Check for image and restaurant, rating
        when {
            selectedRestaurant == 0 -> Toast.makeText(this, "Select restaurant", Toast.LENGTH_SHORT).show()
            enteredRating < 1 -> Toast.makeText(this, "Minimum rating is 1 star", Toast.LENGTH_SHORT).show()
            imageUri == null -> Toast.makeText(this, "Select image!", Toast.LENGTH_SHORT).show()

            else -> {
                // Upload Post
                val post = EnteredPost(selectedRestaurant, enteredRating, enteredReview, imageUri.toString())
                postsRef!!.child(selectedRestaurant.toString() + System.currentTimeMillis().toString()).setValue(post)

                Toast.makeText(this, "Post Submitted", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun selectImage() {
        // Launch the photo picker and let the user choose only images.
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }


    private data class EnteredPost(
        val restaurant: Int,
        val rating: Float,
        val review: String,
        val image: String)
}