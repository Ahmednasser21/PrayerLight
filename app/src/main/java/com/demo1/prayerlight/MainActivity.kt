package com.demo1.prayerlight

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.demo1.prayerlight.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        =========== Handling Tab layout=================
        binding.viewerPager.adapter = PagerAdapter(this)

        val tabLayoutMediator = TabLayoutMediator(binding.mainTabLayout,binding.viewerPager)
            { tab, position ->
                when (position) {
                    0 -> {
                        tab.icon = ContextCompat.getDrawable(
                            this, R.drawable.ic_prayer
                        )
                        tab.text = ContextCompat.getString(this, R.string.pray)
                    }

                    1 -> {
                        tab.icon = ContextCompat.getDrawable(
                            this, R.drawable.outline_home_app_logo_24
                        )
                        tab.text = ContextCompat.getString(this, R.string.home)
                    }

                    2 -> {
                        tab.icon = ContextCompat.getDrawable(
                            this, R.drawable.ic_prayer_beads
                        )
                        tab.text = ContextCompat.getString(this, R.string.azkar)

                    }
                }
            }
        tabLayoutMediator.attach()

        binding.viewerPager.setCurrentItem(1,true)

        binding.mainTabLayout.addOnTabSelectedListener (object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected (tab: TabLayout.Tab?) {

                when (tab?.position) {
                    0 -> supportActionBar?.title = "Prayer Times"
                    2 -> supportActionBar?.title = "Azkar"
                    1 -> supportActionBar?.title = "Prayer Light"
                }
            }

            override fun onTabUnselected (tab: TabLayout.Tab?) {
                if (tab?.position == 1) supportActionBar?.title = "Prayer Light"

            }

            override fun onTabReselected (tab: TabLayout.Tab?) {
                if (tab?.position == 1) supportActionBar?.title = "Prayer Light"
            }
        })
//        =========== End of handling Tab layout =================


    }


//    ================ option menu =================================
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main,menu)

//    == Handling sign in option ==
        val signInItem = menu?.findItem(R.id.signin)
        val signInLayout = signInItem?.actionView
        signInLayout?.setOnClickListener {
            Toast.makeText(this, signInItem.title, Toast.LENGTH_SHORT).show()
        }
//    == End of handling sign in option ==

            return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when ( item.itemId) {
            R.id.settings ->  Toast.makeText(this,item.title,Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }
//    ================ End of option menu ====================
}