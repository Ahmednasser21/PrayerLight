 package com.demo1.prayerlight

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.demo1.prayerlight.databinding.ActivityAlarmSettingsBinding

 class AlarmSettings : AppCompatActivity() {
    private lateinit var binding: ActivityAlarmSettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAlarmSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.previous.setOnClickListener{
            val intent = Intent (this , LocationActivity::class.java)
            startActivity(intent)
        }
    }
}