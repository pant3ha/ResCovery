package com.example.rescovery.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rescovery.ImageUtils.decode
import com.example.rescovery.R
import com.example.rescovery.data.Post
import com.example.rescovery.ui.home.fragments.RestaurantViewModel
import org.w3c.dom.Text

//post adapter is used in any fragment displaying a list of posts
//each item is a full post with all the post and restaurant details
class PostAdapter (private var posts: List<Post>, private val restaurantViewModel: RestaurantViewModel, val onPostClick: ((Post) -> Unit)? = null) :  RecyclerView.Adapter<PostAdapter.ImageViewHolder> () {

    //var onPostClick : ((Post) -> Unit)? = null
    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val postImage: ImageView = itemView.findViewById(R.id.photo_container)
        val postPublisher: TextView = itemView.findViewById(R.id.user_name)
        val postRestaurant: TextView = itemView.findViewById(R.id.restaurant_name)
        val postRestaurantAddress: TextView = itemView.findViewById(R.id.restaurant_address)
        val postRating: RatingBar = itemView.findViewById(R.id.rating)
        val postComment: TextView = itemView.findViewById(R.id.comments)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return ImageViewHolder(view)
    }

    //set up values and display in appropriate views
    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val post = posts[position]

        //use ImageUtils to decode from string
        val decodedBitmap = decode(post.image ?: "")

        // Check if decoding was successful
        if (decodedBitmap != null) {
            Glide.with(holder.itemView.context)
                .load(decodedBitmap) // Directly load the decoded Bitmap
                .placeholder(R.drawable.placeholder_image)
                .into(holder.postImage)
        } else {
            Glide.with(holder.itemView.context)
                .load(R.drawable.placeholder_image) // Use a placeholder if decoding fails
                .into(holder.postImage)
        }
        //display user post details
        holder.postPublisher.text = post.publisher ?: "Unknown Publisher"
        holder.postComment.text = post.review ?: "Unknown"
        holder.postRating.rating = (post.rating)!!

        //get the restaurant details from the restaurantId
        post.restaurant?.let { restaurantId ->
            restaurantViewModel.getRestaurantDetails(restaurantId)
            restaurantViewModel.restaurant.observeForever { restaurant ->
                if (restaurant != null) {
                    //display restaurant details
                    holder.postRestaurant.text = restaurant.restaurantName
                    holder.postRestaurantAddress.text = restaurant.restaurantAddress
                } else {
                    holder.postRestaurant.text = "Unknown"
                }
            }
        }

        // Handle click on the post
        holder.itemView.setOnClickListener { onPostClick?.invoke(post) }
    }

    override fun getItemCount(): Int = posts.size

    //update data when new post is made
    fun updateData(newPosts: List<Post>) {
        posts = newPosts
        notifyDataSetChanged()
    }
}