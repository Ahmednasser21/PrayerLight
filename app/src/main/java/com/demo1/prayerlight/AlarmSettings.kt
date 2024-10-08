 package com.demo1.prayerlight

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.demo1.prayerlight.databinding.ActivityAlarmSettingsBinding

 class AlarmSettings : AppCompatActivity() {
     private lateinit var binding: ActivityAlarmSettingsBinding
     override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         binding = ActivityAlarmSettingsBinding.inflate(layoutInflater)
         setContentView(binding.root)
         binding.previous.setOnClickListener {
             val intent = Intent(this, LanguageActivity::class.java)
             startActivity(intent)
         }
         binding.next.setOnClickListener {
             val intent = Intent(this, LocationActivity::class.java)
             startActivity(intent)
         }
         WindowCompat.setDecorFitsSystemWindows(window, false)
     }
 }