package com.example.rescovery.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rescovery.post_data.Post
import com.google.firebase.database.FirebaseDatabase

class ProfileViewModel : ViewModel() {

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> get() = _posts

    //gets posts associated with a specific user by matching publisher field and username field
    fun getUserPosts(username: String) {
        val postsRef = FirebaseDatabase.getInstance().getReference("posts")
        postsRef.orderByChild("publisher").equalTo(username).get().addOnSuccessListener { snapshot ->
            val postList = mutableListOf<Post>()
            for (child in snapshot.children) {
                val post = child.getValue(Post::class.java)
                if (post != null) postList.add(post)
            }
            _posts.value = postList
        }.addOnFailureListener {
            _posts.value = emptyList()
        }
    }
}