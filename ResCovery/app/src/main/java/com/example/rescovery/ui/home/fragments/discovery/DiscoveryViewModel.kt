package com.example.rescovery.ui.home.fragments.discovery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rescovery.post_data.Post
import com.google.firebase.database.FirebaseDatabase

class DiscoveryViewModel : ViewModel() {
    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> get() = _posts

    init {
        getPosts()
    }

    private fun getPosts() {
        val postsRef = FirebaseDatabase.getInstance().getReference("posts")
        postsRef.get().addOnSuccessListener { snapshot ->
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