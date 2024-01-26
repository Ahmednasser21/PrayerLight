package com.demo1.prayerlight

import android.Manifest
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.batoulapps.adhan2.CalculationMethod
import com.batoulapps.adhan2.Coordinates
import com.batoulapps.adhan2.Madhab
import com.batoulapps.adhan2.Prayer
import com.batoulapps.adhan2.PrayerAdjustments
import com.batoulapps.adhan2.PrayerTimes
import com.batoulapps.adhan2.data.DateComponents
import com.demo1.prayerlight.databinding.FragmentMainBinding
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnFailureListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.chrono.HijrahDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class Main : Fragment() {
    private lateinit var binding: FragmentMainBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val locationRequestCode = 100
    private val requistCheckSettings = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentMainBinding.inflate(inflater, container, false)


//        ================================ prayer time handling ==================
        if (checkSelfPermission()) {

            fusedLocationProviderClient.getCurrentLocation(
             Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                CancellationTokenSource().token
            )
                .addOnSuccessListener { location ->
                    val longitude = location.longitude
                    val latitude = location.latitude

//            ====== Implementing prayerTimes ========
                    val coordinates = Coordinates(latitude, longitude)
                    val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                    val month = Calendar.getInstance().get(Calendar.MONTH) + 1
                    val year = Calendar.getInstance().get(Calendar.YEAR)
                    var date = DateComponents(
                        year,
                        month,
                        day
                    ) // var to update later with the next day to get next day fajr time
                    val params = CalculationMethod.EGYPTIAN.parameters
                        .copy(
                            madhab = Madhab.SHAFI, prayerAdjustments =
                            PrayerAdjustments(dhuhr = -1)
                        )
                    var prayerTimes = PrayerTimes(coordinates, date, params)

//            ====== Formatter and each pray time =======
                    val formatter by lazy { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
                        .apply { TimeZone.getTimeZone("Africa/Cairo") }
                    val sunriseTimee =
                        formatter.format(Date(prayerTimes.sunrise.toEpochMilliseconds()))
                    var fajrTimee =
                        formatter.format(Date(prayerTimes.fajr.toEpochMilliseconds()))
                    val dhuhrTime =
                        formatter.format(Date(prayerTimes.dhuhr.toEpochMilliseconds()))
                    val asrTimee =
                        formatter.format(Date(prayerTimes.asr.toEpochMilliseconds()))
                    val maghribTimee =
                        formatter.format(Date(prayerTimes.maghrib.toEpochMilliseconds()))
                    val ishaTime =
                        formatter.format(Date(prayerTimes.isha.toEpochMilliseconds()))

//            ========= Next pray===========
                    val now = Clock.System.now()
                    val currentPrayer = prayerTimes.currentPrayer(now)
                    var nextPrayer = prayerTimes.nextPrayer(now)

                    var nextPrayerTime = prayerTimes.timeForPrayer(nextPrayer)
                    if (currentPrayer == Prayer.ISHA) {
                        nextPrayer = Prayer.FAJR
                        val currentDate = LocalDate.of(year, month, day)
                        val nextDay = currentDate.plusDays(1)
                        val nextDayFormatted = DateTimeFormatter.ofPattern("dd")
                        val nextReformattedDay = nextDay.format(nextDayFormatted).toInt()
                        date = DateComponents(year, month, nextReformattedDay)
                        prayerTimes = PrayerTimes(coordinates, date, params)
                        nextPrayerTime = prayerTimes.timeForPrayer(nextPrayer)
                        fajrTimee = formatter.format(Date(prayerTimes.fajr.toEpochMilliseconds()))
                    }
                    val nextPrayerTimeFormatted =
                        formatter.format((nextPrayerTime?.toEpochMilliseconds())) // format for the next pray time as string

                    val diff = nextPrayerTime?.toEpochMilliseconds()!!
                        .minus(now.toEpochMilliseconds()) // get difference to use in countdown

//            == Gregorian date==
                    val dateGregorian = LocalDate.now()
                    val formatterGregorian = DateTimeFormatter.ofPattern("EEEE. dd MMMM")
                    val formattedGregorianDate = dateGregorian.format(formatterGregorian)
//            == Hijri date ==
                    val hijrahDate = HijrahDate.now()
                    val formatterHijrah = DateTimeFormatter.ofPattern("d MMMM yyyy 'H'")
                    val formattedHijriDate = hijrahDate.format(formatterHijrah)

                    // Use coroutines to perform geocoding and countdown in the background

                    lifecycleScope.launch {
                        // Get the place name from the geocoder
                        val placeName = withContext(Dispatchers.IO) {
                            val geocoder = Geocoder(requireContext(), Locale.getDefault())
                            val address =
                                geocoder.getFromLocation(latitude, longitude, 1)?.firstOrNull()
                            if (address != null) {
                                val cityName = address.subAdminArea
                                val governmentName = address.adminArea
                                val countryName = address.countryName
                                "$cityName, $governmentName, $countryName"
                            } else {
                                "Unknown location"
                            }
                        }
                        //            ==== updating the UI with place name =========
                        withContext(Dispatchers.Main) {
                            binding.apply {
                                fajrTime.text = fajrTimee
                                sunriseTime.text = sunriseTimee
                                zohrTime.text = dhuhrTime
                                asrTime.text = asrTimee
                                maghribTime.text = maghribTimee
                                eshaaTime.text = ishaTime
                                birthCalender.text = formattedGregorianDate
                                higriCalender.text = formattedHijriDate
                                nextPrayName.text = nextPrayer.toString()
                                nextPrayTime.text = nextPrayerTimeFormatted
                                locationText.text = placeName
                            }
                        }
                        // ======= Start the countdown timer ==============

                        val countDownTimer = object : CountDownTimer(diff, 1000) {
                            override fun onTick(millisUntilFinished: Long) {
                                val hours = millisUntilFinished / 3600000
                                val minutes = (millisUntilFinished % 3600000) / 60000
                                val seconds = (millisUntilFinished % 60000) / 1000
                                val remainingTime = "-$hours:$minutes:$seconds"
                                binding.countdown.text = remainingTime
                            }

                            override fun onFinish() {
                                binding.countdown.text = buildString {
                                    append(getString(R.string.its))
                                    append(nextPrayer)
                                    append(getString(R.string.time))
                                }
                            }
                        }
                        countDownTimer.start()
                    }
                }.addOnFailureListener {
                    OnFailureListener { exception ->
                        if (exception is ResolvableApiException) {
                            try {
                                exception.startResolutionForResult(
                                    requireActivity(),
                                    requistCheckSettings
                                )
                            } catch (sendEx: IntentSender.SendIntentException) {
                                // Ignore the error.
                            }
                        }
                    }
                }
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.location_permission_msg),
                Toast.LENGTH_LONG
            ).show()
            requestLocationPermission()
        }
//        ================================ end of prayer time handling ==================

        return binding.root
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
            locationRequestCode
        )
    }

    private fun checkSelfPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    }
}