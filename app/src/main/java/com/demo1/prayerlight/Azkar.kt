package com.demo1.prayerlight

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.demo1.prayerlight.databinding.FragmentAzkarBinding
import java.time.chrono.HijrahDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class Azkar : Fragment() {
    private lateinit var binding: FragmentAzkarBinding
    private lateinit var month:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAzkarBinding.inflate(inflater,container,false)
        var hijrahDate = HijrahDate.now()

        binding.plus.setOnClickListener{
          hijrahDate=  hijrahDate.plus(1,ChronoUnit.MONTHS)
            month = hijrahDate.format(DateTimeFormatter.ofPattern("MMMM yyyy 'H'"))
            binding.date.text = month

        }

        return binding.root
    }
}


