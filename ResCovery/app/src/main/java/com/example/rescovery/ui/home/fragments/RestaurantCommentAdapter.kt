package com.example.rescovery.ui.home.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rescovery.R
import com.example.rescovery.Restaurant
import com.example.rescovery.UserInput
import com.example.rescovery.post_data.Post
import com.example.rescovery.user_data.User

class RestaurantCommentAdapter(private var posts: List<Post>, private val onItemClick: (Post) -> Unit) : RecyclerView.Adapter<RestaurantCommentAdapter.CommentViewHolder>() {

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.comment_user_name)
        val commentText: TextView = itemView.findViewById(R.id.comment_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val post = posts[position]

        holder.userName.text = post.publisher ?: "Anonymous"
        holder.commentText.text = post.review ?: ""

        holder.itemView.setOnClickListener {
            onItemClick(post)
        }
    }

    override fun getItemCount(): Int = posts.size

    fun updateData(newPosts: List<Post>) {
        posts = newPosts
        notifyDataSetChanged()
    }
}