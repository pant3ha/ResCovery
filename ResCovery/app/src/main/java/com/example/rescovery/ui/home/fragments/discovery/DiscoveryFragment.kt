package com.example.rescovery.ui.home.fragments.discovery

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.helper.widget.Grid
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rescovery.R
import com.example.rescovery.adapters.PostAdapter
import android.content.Intent
import androidx.lifecycle.Observer

class DiscoveryFragment : Fragment() {

    companion object {
        fun newInstance() = DiscoveryFragment()
    }

    private val viewModel: DiscoveryViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_discovery, container, false)
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = GridLayoutManager(context, 3)

        adapter = PostAdapter(emptyList())
        recyclerView.adapter = adapter

        // Show post details activity
//        adapter.onPostClick = {
//            val intent = Intent(requireContext(), PostDetailsActivity::class.java)
//            intent.putExtra("post", it)
//            startActivity(intent)
//        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.posts.observe(viewLifecycleOwner, Observer { posts ->
            adapter.updateData(posts)
        })
    }
}