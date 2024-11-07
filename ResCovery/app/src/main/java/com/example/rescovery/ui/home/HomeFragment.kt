package com.example.rescovery.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.rescovery.databinding.FragmentHomeBinding
import com.example.rescovery.ui.home.fragments.discovery.DiscoveryFragment
import com.example.rescovery.ui.home.fragments.map.MapFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.tabs.TabLayoutMediator.TabConfigurationStrategy
import java.util.ArrayList

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private lateinit var fragmentDiscovery: DiscoveryFragment
    private lateinit var fragmentMap: MapFragment
    private lateinit var viewPager2: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var myFragmentStateAdapter: MyFragmentStateAdapter
    private lateinit var fragments: ArrayList<Fragment>
    private val tabTitles = arrayOf("DISCOVERY", "MAP") //Tab titles
    private lateinit var tabConfigurationStrategy: TabConfigurationStrategy
    private lateinit var tabLayoutMediator: TabLayoutMediator

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewPager2 = binding.viewpager
        tabLayout = binding.tab

        // Initialize fragments
        fragmentDiscovery = DiscoveryFragment()
        fragmentMap = MapFragment()
        fragments = arrayListOf(fragmentDiscovery, fragmentMap)

        myFragmentStateAdapter = MyFragmentStateAdapter(this, fragments)
        viewPager2.adapter = myFragmentStateAdapter

        tabConfigurationStrategy = TabConfigurationStrategy {
                tab: TabLayout.Tab, position: Int ->
            tab.text = tabTitles[position] }
        tabLayoutMediator = TabLayoutMediator(tabLayout, viewPager2, tabConfigurationStrategy)
        tabLayoutMediator.attach()



        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}