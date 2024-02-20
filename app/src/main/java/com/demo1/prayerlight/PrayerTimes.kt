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
import com.demo1.prayerlight.databinding.RecyclerRowBinding
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.chrono.HijrahDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import java.util.Date
import java.util.Locale
import java.util.TimeZone


class PrayerTimes : Fragment() {
    private lateinit var binding: FragmentPrayerTimesBinding
    private var currentCalendar = "hijrah"
    private var longitude:Double = 0.0
    private var latitude:Double =0.0
    private lateinit var coordinates:Coordinates
   private val params = CalculationMethod.EGYPTIAN.parameters
        .copy(
            madhab = Madhab.SHAFI, prayerAdjustments =
            PrayerAdjustments(dhuhr = -1)
        )
//    ========Gregorian date =============
    private val dateGregorian = LocalDate.now()
    private val gregorianFormatter =DateTimeFormatter.ofPattern("MMMM yyyy")
    private var day = dateGregorian.dayOfMonth
    private var  month = dateGregorian.monthValue
    private var year = dateGregorian.year
    private var currentDate = LocalDate.of(year, month, day)
    private val dayFormatter = DateTimeFormatter.ofPattern("dd")
    private val monthFormatter = DateTimeFormatter.ofPattern("MM")
    private val yearFormatter = DateTimeFormatter.ofPattern("yyyy")
//    ======= hijrah date =========
    private var hijrahDate = HijrahDate.now()
    private val formatterHijrah = DateTimeFormatter.ofPattern("MMMM yyyy 'H'")



    private lateinit var spinnerText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        ========= getting longitude and latitude ========
        val preferences = requireActivity().getSharedPreferences("location_data", Context.MODE_PRIVATE)

        if (preferences.contains("latitude") && preferences.contains("longitude")) {
            longitude = preferences.getFloat("longitude", 0f).toDouble()
            latitude = preferences.getFloat("latitude", 0f).toDouble()
        }
        coordinates = Coordinates(latitude, longitude)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPrayerTimesBinding.inflate(inflater,container,false)
        val spinner = binding.spinner

//        =======================Recycler view ================================
        var prayerTimesArray = ArrayList<PrayerTimesForArray>()
        val recyclerView = binding.pTRecycler
        var recyclerAdapter = PrayerTimesAdapter(prayerTimesArray)
        recyclerView.adapter = recyclerAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL,false)

//        ==========================Spinner======================

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
//         ======= Gregorian date =============
                val formattedGregorianDate = dateGregorian.format(gregorianFormatter)
//         ========= Hijrah date ============
                val formattedHijriDate = hijrahDate.format(formatterHijrah)

                 spinnerText = view as TextView

                when (position){


                    0-> { spinnerText.text = formattedHijriDate
                        // initialize the recyclerAdapter with the updated prayerTimesArray
                        prayerTimesArray = hijrahCalender()
                        recyclerAdapter = PrayerTimesAdapter(prayerTimesArray)
                        recyclerView.adapter = recyclerAdapter
                        recyclerAdapter.notifyDataSetChanged()

                    }
                    1-> { spinnerText.text =formattedGregorianDate
                        // initialize the recyclerAdapter with the updated prayerTimesArray
                        prayerTimesArray = gregorianCalender()
                        recyclerAdapter = PrayerTimesAdapter(prayerTimesArray)
                        recyclerView.adapter = recyclerAdapter
                        recyclerAdapter.notifyDataSetChanged()

                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
        binding.nextMonth.setOnClickListener {
            val updatedPrayerTimesArray: ArrayList<PrayerTimesForArray> =
                if (currentCalendar == "hijrah") {
                    hijrahDate = hijrahDate.plus(1,ChronoUnit.MONTHS)
                    spinnerText.text = hijrahDate.format(formatterHijrah)
                    hijrahCalender()

                } else {
                    currentDate = currentDate.plusMonths(1)
                    month = currentDate.monthValue
                    spinnerText.text = currentDate.format(gregorianFormatter)
                    gregorianCalender()
                }
                // Update the RecyclerView adapter to display the new data
                recyclerAdapter.updateData(updatedPrayerTimesArray)
        }

        binding.previosMonth.setOnClickListener {

            val updatedPrayerTimesArray: ArrayList<PrayerTimesForArray> =
                if (currentCalendar == "hijrah") {
                   hijrahDate = hijrahDate.minus(1,ChronoUnit.MONTHS)
                    spinnerText.text = hijrahDate.format(formatterHijrah)
                    hijrahCalender()

                } else {
                    currentDate = currentDate.minusMonths(1)
                    month = currentDate.monthValue
                    spinnerText.text = currentDate.format(gregorianFormatter)
                    gregorianCalender()
                }
                // Update the RecyclerView adapter to display the new data
                recyclerAdapter.updateData(updatedPrayerTimesArray)
            }

        
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

//    ==================Recycler View Adapter ==============
    inner class PrayerTimesAdapter (private var prayerTimes: List<PrayerTimesForArray>) :
        RecyclerView.Adapter<PrayerTimesAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = RecyclerRowBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            val prayerTimesPosition = prayerTimes[position]

            holder.bindData(prayerTimesPosition)

            val dayForHolder = hijrahDate.format(dayFormatter).toInt()
            val monthHijreah = hijrahDate.format(monthFormatter)
            val yearHijrah = hijrahDate.format(yearFormatter)

//             Highlight current day cell dynamically

            if (prayerTimesPosition.day % 2 == 0) {
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context!!, R.color.gary))
            }
            else {
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context!!, R.color.white))
            }

            if (currentCalendar == "hijrah"){
                if (prayerTimesPosition.day == dayForHolder
                    && monthHijreah ==HijrahDate.now().format(monthFormatter)
                    &&yearHijrah == HijrahDate.now().format(yearFormatter)) {
                    holder.itemView.setBackgroundColor(
                        ContextCompat.getColor(context!!, R.color.second)
                    )
                    holder.day.setTextColor(ContextCompat.getColor(context!!, R.color.golden))
                    holder.fajr.setTextColor(ContextCompat.getColor(context!!, R.color.golden))
                    holder.dhuhr.setTextColor(ContextCompat.getColor(context!!, R.color.golden))
                    holder.asr.setTextColor(ContextCompat.getColor(context!!, R.color.golden))
                    holder.maghrib.setTextColor(ContextCompat.getColor(context!!, R.color.golden))
                    holder.isha.setTextColor(ContextCompat.getColor(context!!, R.color.golden))
                }else{
                    holder.day.setTextColor(ContextCompat.getColor(context!!, R.color.second))
                    holder.fajr.setTextColor(ContextCompat.getColor(context!!, R.color.second))
                    holder.dhuhr.setTextColor(ContextCompat.getColor(context!!, R.color.second))
                    holder.asr.setTextColor(ContextCompat.getColor(context!!, R.color.second))
                    holder.maghrib.setTextColor(ContextCompat.getColor(context!!, R.color.second))
                    holder.isha.setTextColor(ContextCompat.getColor(context!!, R.color.second))

                }

            }
            else{

            if (prayerTimesPosition.day == day && year == currentDate.year
                && month == dateGregorian.monthValue){
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context!!, R.color.second))
                holder.day.setTextColor(ContextCompat.getColor(context!!, R.color.golden))
                holder.fajr.setTextColor(ContextCompat.getColor(context!!, R.color.golden))
                holder.dhuhr.setTextColor(ContextCompat.getColor(context!!, R.color.golden))
                holder.asr.setTextColor(ContextCompat.getColor(context!!, R.color.golden))
                holder.maghrib.setTextColor(ContextCompat.getColor(context!!, R.color.golden))
                holder.isha.setTextColor(ContextCompat.getColor(context!!, R.color.golden))
            }else{
                holder.day.setTextColor(ContextCompat.getColor(context!!, R.color.second))
                holder.fajr.setTextColor(ContextCompat.getColor(context!!, R.color.second))
                holder.dhuhr.setTextColor(ContextCompat.getColor(context!!, R.color.second))
                holder.asr.setTextColor(ContextCompat.getColor(context!!, R.color.second))
                holder.maghrib.setTextColor(ContextCompat.getColor(context!!, R.color.second))
                holder.isha.setTextColor(ContextCompat.getColor(context!!, R.color.second))

            }

            }
        }


//    =============view holder class ============
        override fun getItemCount(): Int = prayerTimes.size
        fun updateData(newPrayerTimes: List<PrayerTimesForArray>) {
         // Update the adapter's internal data list
          this.prayerTimes = newPrayerTimes

         // Notify the adapter that the data has changed
        notifyDataSetChanged()
        }

        inner class ViewHolder(binding: RecyclerRowBinding) : RecyclerView.ViewHolder(binding.root) {
            val day = binding.day
            val fajr = binding.fajrTime
            val dhuhr = binding.dhuhrTime
            val asr = binding.asrTime
            val maghrib = binding.maghribTime
            val isha = binding.eshaaTime
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


//    ================= Gregorian calender ==============
    private fun gregorianCalender(): ArrayList<PrayerTimesForArray>{

        val prayerTimesArray = ArrayList<PrayerTimesForArray>()

//        ============= first and last day of the month ========
        val firstDayOfMonthG = LocalDate.of(year,month,1)
        val formattedFirstDayOfMonthG = firstDayOfMonthG.dayOfMonth

        val lastDayOfMonthG = dateGregorian.with(TemporalAdjusters.lastDayOfMonth())
        val formattedLastDayOfMonthG = lastDayOfMonthG.dayOfMonth



        for ( days in formattedFirstDayOfMonthG..formattedLastDayOfMonthG){
//           ======== prayer Times configration ==========
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

        return prayerTimesArray
    }

    private fun hijrahCalender(): ArrayList<PrayerTimesForArray>{

        val prayerTimesArray = ArrayList<PrayerTimesForArray>()

//        ============= first and last day of the month ========


        val firstDayOfMonthH = hijrahDate.with(TemporalAdjusters.firstDayOfMonth())
        val formattedFirstDayOfMonthH = firstDayOfMonthH.format(dayFormatter).toInt()

        val lastDayOfMonthH = hijrahDate.with(TemporalAdjusters.lastDayOfMonth())
        val formattedLastDayOfMonthH = lastDayOfMonthH.format(dayFormatter).toInt()

        val monthHijrah = hijrahDate.format(monthFormatter).toInt()
        val yearHijrah = hijrahDate.format(yearFormatter).toInt()
//        ========= prayer times ==============


        for ( days in formattedFirstDayOfMonthH..formattedLastDayOfMonthH){

                val hijriDate = HijrahDate.of(yearHijrah, monthHijrah, days)
                // convert it to a Gregorian date
                val gregorianDate = LocalDate.from(hijriDate)
                // get the Gregorian date components
                val gregorianYearFromHijrah = gregorianDate.year
                val gregorianMonthFromHijrah = gregorianDate.monthValue
                val gregorianDayFromHijrah = gregorianDate.dayOfMonth

                val date = DateComponents(gregorianYearFromHijrah, gregorianMonthFromHijrah, gregorianDayFromHijrah)
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

        return prayerTimesArray
    }


}