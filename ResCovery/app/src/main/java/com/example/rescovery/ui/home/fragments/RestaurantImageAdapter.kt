package com.example.rescovery.ui.home.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.bumptech.glide.Glide
import com.example.rescovery.R

class RestaurantImageAdapter(private val imageUrls: List<String>) : RecyclerView.Adapter<RestaurantImageAdapter.ImageViewHolder>() {
    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.image_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUrl = if (imageUrls.isEmpty()) null else imageUrls[position]
        Glide.with(holder.imageView.context).load(imageUrl).placeholder(R.drawable.placeholder_image).into(holder.imageView)
    }

    override fun getItemCount(): Int = if (imageUrls.isEmpty()) 1 else imageUrls.size
}