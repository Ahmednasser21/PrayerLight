package com.demo1.prayerlight

import PrayerTimesForArray
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.batoulapps.adhan2.CalculationMethod
import com.batoulapps.adhan2.Coordinates
import com.batoulapps.adhan2.Madhab
import com.batoulapps.adhan2.PrayerAdjustments
import com.batoulapps.adhan2.PrayerTimes
import com.batoulapps.adhan2.data.DateComponents
import com.demo1.prayerlight.databinding.FragmentPrayerTimesBinding
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.chrono.HijrahDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone


class PrayerTimes : Fragment() {
    private lateinit var binding: FragmentPrayerTimesBinding
    private var currentCalendar = "hijrah"
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
        val formatterGregorian = DateTimeFormatter.ofPattern("MMMM yyyy")
        val formattedGregorianDate = dateGregorian.format(formatterGregorian)
//         ========= Hijrah date ============
        val hijrahDate = HijrahDate.now()
        val formatterHijrah = DateTimeFormatter.ofPattern("MMMM yyyy 'H'")
        val formattedHijriDate = hijrahDate.format(formatterHijrah)

//        ============= PrayerTimes list =================
        var prayerTimesArray =ArrayList<PrayerTimesForArray>()

        //  ======== spinner handling ===========

//        =======================================================
        val recyclerView = binding.pTRecycler
        var recyclerAdapter = PrayerTimesAdapter(prayerTimesArray)
        recyclerView.adapter = recyclerAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL,false)
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
                currentCalendar = if (position == 0) "hijrah" else "gregorian"
                val textView = view as TextView

                when (position){


                    0-> {textView.text = formattedHijriDate
                        prayerTimesArray = hijrahCalender()
                        // initialize the recyclerAdapter with the updated prayerTimesArray
                        recyclerAdapter = PrayerTimesAdapter(prayerTimesArray)
                        // set the recyclerAdapter to the recyclerView
                        recyclerView.adapter = recyclerAdapter
                        // notify the adapter about the data change
                        recyclerAdapter.notifyDataSetChanged()

                    }
                    1-> {textView.text =formattedGregorianDate

                        prayerTimesArray = gregorianCalender()
                        // initialize the recyclerAdapter with the updated prayerTimesArray
                        recyclerAdapter = PrayerTimesAdapter(prayerTimesArray)
                        // set the recyclerAdapter to the recyclerView
                        recyclerView.adapter = recyclerAdapter
                        // notify the adapter about the data change
                        recyclerAdapter.notifyDataSetChanged()

                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        // Handle month navigation buttons
//        binding.previosMonth.setOnClickListener {
//            currentMonth--
//            getPrayerTimesForMonth(currentMonth)
//            adapter.updateData(prayerTimes)
//        }
//        binding.nextMonth.setOnClickListener {
//            currentMonth++
//            getPrayerTimesForMonth(currentMonth)
//            adapter.updateData(prayerTimes)
//        }
//
//        // Handle view mode buttons
//        binding.days30.setOnClickListener {
//            fullMonthView = true
//            adapter.updateData(prayerTimes.slice(0..30)) // Assuming 31 days in current month
//        }
//        binding.days7.setOnClickListener {
//            fullMonthView = false
//            // Get current day of week and calculate visible days
//            val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
//            val startDay = (currentDay - 1) % 7
//            val endDay = startDay + 6
//            adapter.updateData(prayerTimes.slice(startDay..endDay))
//        }

        return binding.root
    }
    inner class PrayerTimesAdapter(private val prayerTimes: List<PrayerTimesForArray>) :
        RecyclerView.Adapter<PrayerTimesAdapter.ViewHolder>() {

        private val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        private val formatter = DateTimeFormatter.ofPattern("hh:mm")

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_row, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val prayerTime = prayerTimes[position]
            holder.bindData(prayerTime)
            val hijrahDate = HijrahDate.now()
            val dayFormatter = DateTimeFormatter.ofPattern("dd")
            val dayForHolder = hijrahDate.format(dayFormatter).toInt()

//             Highlight current day cell dynamically

            if (prayerTime.day % 2 == 0) {
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context!!, R.color.gary))
            }
            else {
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context!!, R.color.white))
            }

            if (currentCalendar == "hijrah"){
                if (prayerTime.day == dayForHolder ) {
                    holder.itemView.setBackgroundColor(
                        ContextCompat.getColor(
                            context!!,
                            R.color.second
                        )
                    )
                    holder.day.setTextColor(ContextCompat.getColor(context!!, R.color.golden))
                    holder.fajr.setTextColor(ContextCompat.getColor(context!!, R.color.golden))
                    holder.dhuhr.setTextColor(ContextCompat.getColor(context!!, R.color.golden))
                    holder.asr.setTextColor(ContextCompat.getColor(context!!, R.color.golden))
                    holder.maghrib.setTextColor(ContextCompat.getColor(context!!, R.color.golden))
                    holder.isha.setTextColor(ContextCompat.getColor(context!!, R.color.golden))
                }

            }
            else{
            if (prayerTime.day == Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) {
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context!!, R.color.second))
                holder.day.setTextColor(ContextCompat.getColor(context!!, R.color.golden))
                holder.fajr.setTextColor(ContextCompat.getColor(context!!, R.color.golden))
                holder.dhuhr.setTextColor(ContextCompat.getColor(context!!, R.color.golden))
                holder.asr.setTextColor(ContextCompat.getColor(context!!, R.color.golden))
                holder.maghrib.setTextColor(ContextCompat.getColor(context!!, R.color.golden))
                holder.isha.setTextColor(ContextCompat.getColor(context!!, R.color.golden))
            }

            }
        }

        override fun getItemCount(): Int = prayerTimes.size

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val day: TextView = itemView.findViewById(R.id.day)
            val fajr: TextView = itemView.findViewById(R.id.fajr_time)
            val dhuhr: TextView = itemView.findViewById(R.id.dhuhr_time)
            val asr: TextView = itemView.findViewById(R.id.asr_time)
            val maghrib: TextView = itemView.findViewById(R.id.maghrib_time)
            val isha: TextView = itemView.findViewById(R.id.eshaa_time)
            fun bindData(prayerTime: PrayerTimesForArray) {
                day.text = prayerTime.day.toString()
                fajr.text = prayerTime.fajrTime
                dhuhr.text = prayerTime.dhuhrTime
                asr.text =prayerTime.asrTime
                maghrib.text = prayerTime.maghripTime
                isha.text = prayerTime.ishaTime
            }
        }
    }

    private fun gregorianCalender(): ArrayList<PrayerTimesForArray>{
        val dateGregorian = LocalDate.now()

        val prayerTimesArray = ArrayList<PrayerTimesForArray>()
//        ============= first and last day of the month ========
        val dayFormatter = DateTimeFormatter.ofPattern("dd") // formatter
        val firstDayOfMonthG = dateGregorian.with(TemporalAdjusters.firstDayOfMonth())
        val formattedFirstDayOfMonthG = firstDayOfMonthG.format(dayFormatter).toInt()

        val lastDayOfMonthG = dateGregorian.with(TemporalAdjusters.lastDayOfMonth())
        val formattedLastDayOfMonthG = lastDayOfMonthG.format(dayFormatter).toInt()
//        ========= prayer times ==============
        val preferences = requireActivity().getSharedPreferences("location_data", Context.MODE_PRIVATE)
        val longitude:Double
        val latitude:Double
        if (preferences.contains("latitude") && preferences.contains("longitude")) {
            longitude = preferences.getFloat("longitude", 0f).toDouble()
            latitude = preferences.getFloat("latitude", 0f).toDouble()

            val coordinates = Coordinates(latitude, longitude)

            val month = Calendar.getInstance().get(Calendar.MONTH) + 1
            val year = Calendar.getInstance().get(Calendar.YEAR)

            val params = CalculationMethod.EGYPTIAN.parameters
                .copy(
                    madhab = Madhab.SHAFI, prayerAdjustments =
                    PrayerAdjustments(dhuhr = -1)
                )

            for ( days in formattedFirstDayOfMonthG..formattedLastDayOfMonthG){
                val date = DateComponents(year,
                    month,
                    days)
                val prayerTimes = PrayerTimes(coordinates, date, params)
                val formatter by lazy { SimpleDateFormat("hh:mm", Locale.getDefault()) }
                    .apply { TimeZone.getTimeZone("Africa/Cairo") }
                val fajrTimee =
                    formatter.format(Date(prayerTimes.fajr.toEpochMilliseconds()))
                val dhuhrTime =
                    formatter.format(Date(prayerTimes.dhuhr.toEpochMilliseconds()))
                val asrTimee =
                    formatter.format(Date(prayerTimes.asr.toEpochMilliseconds()))
                val maghribTimee =
                    formatter.format(Date(prayerTimes.maghrib.toEpochMilliseconds()))
                val ishaTime =
                    formatter.format(Date(prayerTimes.isha.toEpochMilliseconds()))
                val dayPrayers = PrayerTimesForArray(days,fajrTimee,dhuhrTime,asrTimee,maghribTimee,ishaTime)
                prayerTimesArray.add(dayPrayers)

            }
        }
        return prayerTimesArray
    }

    private fun hijrahCalender(): ArrayList<PrayerTimesForArray>{
        val hijrahDate = HijrahDate.now()

        val prayerTimesArray = ArrayList<PrayerTimesForArray>()
//        ============= first and last day of the month ========
        val dayFormatter = DateTimeFormatter.ofPattern("dd")
        val monthFormatter = DateTimeFormatter.ofPattern("MM")
        val yearFormatter = DateTimeFormatter.ofPattern("yyyy")
//        hijrah date

        val firstDayOfMonthH = hijrahDate.with(TemporalAdjusters.firstDayOfMonth())
        val formattedFirstDayOfMonthH = firstDayOfMonthH.format(dayFormatter).toInt()

        val lastDayOfMonthH = hijrahDate.with(TemporalAdjusters.lastDayOfMonth())
        val formattedLastDayOfMonthH = lastDayOfMonthH.format(dayFormatter).toInt()

//        ========= prayer times ==============
        val preferences = requireActivity().getSharedPreferences("location_data", Context.MODE_PRIVATE)
        val longitude:Double
        val latitude:Double
        if (preferences.contains("latitude") && preferences.contains("longitude")) {
            longitude = preferences.getFloat("longitude", 0f).toDouble()
            latitude = preferences.getFloat("latitude", 0f).toDouble()

            val coordinates = Coordinates(latitude, longitude)

            val month = hijrahDate.format(monthFormatter).toInt()
            val year = hijrahDate.format(yearFormatter).toInt()

            val params = CalculationMethod.EGYPTIAN.parameters
                .copy(
                    madhab = Madhab.SHAFI, prayerAdjustments =
                    PrayerAdjustments(dhuhr = -1)
                )

            for ( days in formattedFirstDayOfMonthH..formattedLastDayOfMonthH){
                val hijriDate = HijrahDate.of(year, month, days)
                // convert it to a Gregorian date
                val gregorianDate = LocalDate.from(hijriDate)
                // get the Gregorian date components
                val gregorianYear = gregorianDate.year
                val gregorianMonth = gregorianDate.monthValue
                val gregorianDay = gregorianDate.dayOfMonth
                val date = DateComponents(gregorianYear, gregorianMonth, gregorianDay)
                val prayerTimes = PrayerTimes(coordinates, date, params)
                val formatter by lazy { SimpleDateFormat("hh:mm", Locale.getDefault()) }
                    .apply { TimeZone.getTimeZone("Africa/Cairo") }
                val fajrTimee =
                    formatter.format(Date(prayerTimes.fajr.toEpochMilliseconds()))
                val dhuhrTime =
                    formatter.format(Date(prayerTimes.dhuhr.toEpochMilliseconds()))
                val asrTimee =
                    formatter.format(Date(prayerTimes.asr.toEpochMilliseconds()))
                val maghribTimee =
                    formatter.format(Date(prayerTimes.maghrib.toEpochMilliseconds()))
                val ishaTime =
                    formatter.format(Date(prayerTimes.isha.toEpochMilliseconds()))
                val dayPrayers = PrayerTimesForArray(days,fajrTimee,dhuhrTime,asrTimee,maghribTimee,ishaTime)
                prayerTimesArray.add(dayPrayers)
            }
        }
        return prayerTimesArray
    }


}