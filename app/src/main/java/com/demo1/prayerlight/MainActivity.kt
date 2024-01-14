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
//        =========== Handling Tab layout=================
        binding.viewerPager.adapter = PagerAdapter(this)

        binding.bottomNavBar.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.pray -> binding.viewerPager.setCurrentItem(0, true)
                R.id.home -> binding.viewerPager.setCurrentItem(1, true)
                R.id.azkar -> binding.viewerPager.setCurrentItem(2, true)
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