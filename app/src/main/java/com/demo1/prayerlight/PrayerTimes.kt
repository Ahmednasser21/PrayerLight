package com.demo1.prayerlight

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.demo1.prayerlight.databinding.FragmentPrayerTimesBinding
import java.time.LocalDate
import java.time.chrono.HijrahDate
import java.time.format.DateTimeFormatter


class PrayerTimes : Fragment() {
    private lateinit var binding: FragmentPrayerTimesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPrayerTimesBinding.inflate(inflater,container,false)
//        ======= Gregorian date =============
        val dateGregorian = LocalDate.now()
        val formatterGregorian = DateTimeFormatter.ofPattern("MMMM")
        val formattedGregorianDate = dateGregorian.format(formatterGregorian)
//         ========= Hijrah date ============
        val hijrahDate = HijrahDate.now()
        val formatterHijrah = DateTimeFormatter.ofPattern("MMMM")
        val formattedHijriDate = hijrahDate.format(formatterHijrah)
//  ======== spinner handling ===========
        val spinner = binding.spinner
        ArrayAdapter.createFromResource(requireContext(),R.array.spinner,R.layout.spinner_item).
            also { adapter->
                adapter.setDropDownViewResource(R.layout.spinner_layout)
                spinner.adapter = adapter
            }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
              val textView = view as TextView

               when (position){

                    0-> textView.text = formattedHijriDate
                    1-> textView.text =formattedGregorianDate
              }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        return binding.root
    }


}