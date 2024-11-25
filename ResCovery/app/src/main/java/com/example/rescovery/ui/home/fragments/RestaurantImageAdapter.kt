package com.example.rescovery.ui.home.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.bumptech.glide.Glide
import com.example.rescovery.R
import com.example.rescovery.Restaurant
import com.example.rescovery.UserInput

class RestaurantImageAdapter(private val userInputs: List<UserInput>,
                             private val restaurant: Restaurant,
                             private val onItemClick: (UserInput) -> Unit) : RecyclerView.Adapter<RestaurantImageAdapter.ImageViewHolder>() {
    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.image_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val userInput = userInputs[position]
        Glide.with(holder.imageView.context).load(userInput.photoUri).placeholder(R.drawable.placeholder_image).error(R.drawable.placeholder_image).into(holder.imageView)
        holder.itemView.setOnClickListener {
            onItemClick(userInput)
        }
    }

    override fun getItemCount(): Int = userInputs.size
}