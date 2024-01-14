package com.demo1.prayerlight

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.demo1.prayerlight.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        =========== Handling Bottom nav view=================
        binding.viewerPager.adapter = PagerAdapter(this)

        binding.bottomNavBar.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.pray -> binding.viewerPager.currentItem = 0
                R.id.home -> binding.viewerPager.currentItem = 1
                R.id.azkar -> binding.viewerPager.currentItem = 2
            }
            when (item.itemId) {
                R.id.pray -> supportActionBar?.title = "Prayer Times"
                R.id.home -> supportActionBar?.title = "Prayer Light"
                R.id.azkar -> supportActionBar?.title = "Azkar"
            }
            true
        }

        binding.bottomNavBar.selectedItemId = R.id.home

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