package com.example.rescovery.ui.home.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rescovery.R
import com.example.rescovery.Restaurant
import com.example.rescovery.UserInput
import com.example.rescovery.user_data.User

class RestaurantCommentAdapter(private val comments: List<UserInput>, private val restaurant: Restaurant, private val onItemClick: (UserInput) -> Unit) : RecyclerView.Adapter<RestaurantCommentAdapter.CommentViewHolder>() {

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.comment_user_name)
        val commentText: TextView = itemView.findViewById(R.id.comment_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val userInput = comments[position]
        holder.userName.text = userInput.userName
        holder.commentText.text = userInput.comment

        holder.itemView.setOnClickListener {
            onItemClick(userInput)
        }
    }

    override fun getItemCount(): Int = comments.size
}