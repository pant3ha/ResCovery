package com.example.rescovery

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AddPostActivity : AppCompatActivity() {

    private lateinit var closeBtn: ImageView
    private lateinit var submitBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_post)

        closeBtn = findViewById(R.id.add_post_close_btn)
        submitBtn = findViewById(R.id.add_post_submit_btn)

        closeBtn.setOnClickListener {
            finish()
        }

        submitBtn.setOnClickListener {
            Toast.makeText(this, "Post Submitted", Toast.LENGTH_SHORT).show()
            finish()
        }

    }
}