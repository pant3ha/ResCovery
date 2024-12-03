package com.example.rescovery.ui.home.fragments

import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.bumptech.glide.Glide
import com.example.rescovery.ImageUtils
import com.example.rescovery.R
import com.example.rescovery.Restaurant
import com.example.rescovery.UserInput
import com.example.rescovery.data.Post

//adapter for imageRecycler in restaurant fragment
class RestaurantImageAdapter(private var posts: List<Post>, private val onItemClick: (Post) -> Unit) : RecyclerView.Adapter<RestaurantImageAdapter.ImageViewHolder>() {

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.image_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        //gets post based on position, translates the image and decodes it to display
        val post = posts[position]
        val bitmap = post.image?.let {ImageUtils.decode(it)}
        if (bitmap != null) {
            holder.imageView.setImageBitmap(bitmap)
            Log.d("RestaurantImageAdapter", "Image at position $position successfully decoded.")
        } else {
            holder.imageView.setImageResource(R.drawable.placeholder_image)
            Log.d("RestaurantImageAdapter", "Failed to decode image at position $position.")
        }

        Log.d("RestaurantImageAdapter", "Binding image at position $position")

        holder.itemView.setOnClickListener {
            onItemClick(post)
        }
    }

    fun updateData(newPosts: List<Post>) {
        posts = newPosts
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = posts.size
}