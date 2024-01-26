package com.demo1.prayerlight

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class PagerAdapter (fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        var fragment : Fragment?= null
        when (position){
            0-> fragment = PrayerTimes()
            1-> fragment = Home()
            2-> fragment = Azkar()
        }
        return fragment!!
    }
}
