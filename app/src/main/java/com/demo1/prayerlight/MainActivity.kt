package com.demo1.prayerlight

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.demo1.prayerlight.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        =========== Handling Bottom nav view=================
        binding.bottomNavBar.setOnItemSelectedListener { item ->
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            when (item.itemId) {
                R.id.home -> {
                    fragmentTransaction.replace(R.id.fragment_container, Home())
                    fragmentTransaction.commit()
                    true

                }
                R.id.pray -> {
                    fragmentTransaction.replace(R.id.fragment_container, PrayerTimes())
                    fragmentTransaction.commit()
                    true
                }
                R.id.azkar -> {
                    fragmentTransaction.replace(R.id.fragment_container, Azkar())
                    fragmentTransaction.commit()
                    true
                }
                else -> false
            }
        }
        binding.bottomNavBar.selectedItemId = R.id.home
        // Making status bar overlaps with the activity
        WindowCompat.setDecorFitsSystemWindows(window, false)
        //        =========== End of handling Bottom nav view =================
    }
//    ================ option menu =================================
//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.menu_main,menu)
//
////    == Handling sign in option ==
//        val signInItem = menu?.findItem(R.id.signin)
//        val signInLayout = signInItem?.actionView
//        signInLayout?.setOnClickListener {
//            Toast.makeText(this, signInItem.title, Toast.LENGTH_SHORT).show()
//        }
////    == End of handling sign in option ==
//
//        return super.onCreateOptionsMenu(menu)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when ( item.itemId) {
//            R.id.settings ->  Toast.makeText(this,item.title,Toast.LENGTH_SHORT).show()
//        }
//        return super.onOptionsItemSelected(item)
//    }
//    ================ End of option menu ====================
}