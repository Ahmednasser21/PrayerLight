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
            1-> fragment = PrayerTimes()
            0-> fragment = Main()
            2-> fragment = Azkar()
        }
        return fragment!!
    }
}
