package com.example.rescovery.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rescovery.data.Post
import com.example.rescovery.data.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileViewModel : ViewModel() {

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> get() = _user

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

    // gets user by username
    suspend fun getUser(username: String) {
        val usersRef = FirebaseDatabase.getInstance().getReference("users")
        usersRef.child(username).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _user.value = snapshot.getValue(User::class.java)
            }

            override fun onCancelled(error: DatabaseError) {
                _user.value = null
            }
        })
    }
}