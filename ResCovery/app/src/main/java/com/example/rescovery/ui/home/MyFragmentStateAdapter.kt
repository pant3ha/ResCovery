package com.example.rescovery.ui.home

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class MyFragmentStateAdapter(fragment: Fragment, var list: ArrayList<Fragment>)
    : FragmentStateAdapter(fragment){
    override fun createFragment(position: Int): Fragment {
        return list[position]
    }

    override fun getItemCount(): Int {
        return list.size
    }
}