package com.example.rescovery.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rescovery.ImageUtils.decode
import com.example.rescovery.R
import com.example.rescovery.post_data.Post

class PostAdapter (private var posts: List<Post>) :  RecyclerView.Adapter<PostAdapter.ImageViewHolder> () {

    var onPostClick : ((Post) -> Unit)? = null

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val postImage: ImageView = itemView.findViewById(R.id.image_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.grid_item, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val post = posts[position]

        // Load the image using Glide
        Glide.with(holder.itemView.context)
            .asBitmap()
            .load(decode(post.image ?: "")) // Decode Base64 string
            .placeholder(R.drawable.placeholder_image)
            .into(holder.postImage)

        // Handle click on the post
        holder.itemView.setOnClickListener { onPostClick?.invoke(post) }
    }

    override fun getItemCount(): Int = posts.size

    fun updateData(newPosts: List<Post>) {
        posts = newPosts
        notifyDataSetChanged()
    }
}