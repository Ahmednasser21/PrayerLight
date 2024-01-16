package com.demo1.prayerlight

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.batoulapps.adhan2.CalculationMethod
import com.batoulapps.adhan2.Coordinates
import com.batoulapps.adhan2.Madhab
import com.batoulapps.adhan2.Prayer
import com.batoulapps.adhan2.PrayerAdjustments
import com.batoulapps.adhan2.PrayerTimes
import com.batoulapps.adhan2.data.DateComponents
import com.demo1.prayerlight.databinding.FragmentMainBinding
import kotlinx.datetime.Clock
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.chrono.HijrahDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class Main : Fragment(), LocationListener {
    private lateinit var binding: FragmentMainBinding
    private lateinit var locationManager: LocationManager
    private var location: Location? = null
    private val locationRequestCode = 100


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }

        locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        //        ===== Get last known location and ask for permission if it is not granted====

        location = when (checkSelfPermission()) {
            true -> {

                locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            }

            false -> {
                requestLocationPermission()

                if (checkSelfPermission()) locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                else Toast.makeText(
                    requireContext(),
                    getString(R.string.location_permission_msg),
                    Toast.LENGTH_SHORT
                ).show()
                null
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)



//        ================================ prayer time handling ==================

        location?.let { loc ->
            val longitude = loc.longitude
            val latitude = loc.latitude
//            ======== Getting the address from location =======
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            val addresses: List<Address> =
                geocoder.getFromLocation(latitude, longitude, 1) as List<Address>
            val cityName = addresses[0].subAdminArea
            val governmentName = addresses[0].adminArea
            val countryName = addresses[0].countryName
            val placeName = "$cityName, $governmentName, $countryName"
//            ===== End of getting the address from location=======
//            ====== Implementing prayerTimes ========
            val coordinates = Coordinates(latitude, longitude)
            var day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            val month = Calendar.getInstance().get(Calendar.MONTH)+1
            val year = Calendar.getInstance().get(Calendar.YEAR)
            var date = DateComponents(year, month, day)
            val params = CalculationMethod.EGYPTIAN.parameters
                .copy(
                    madhab = Madhab.SHAFI, prayerAdjustments =
                    PrayerAdjustments(
                        fajr = 7, dhuhr = -1,
                        asr = -1, isha = -2
                    )
                )
            var prayerTimes = PrayerTimes(coordinates, date, params)
//            ====== End of implementing prayerTimes ========
//            ====== Formatter and each pray time =======
            val formatter = SimpleDateFormat("hh:mm a",Locale.getDefault())
            formatter.timeZone = TimeZone.getTimeZone("Africa/Cairo")

            var fajrTimee =
                formatter.format(Date(prayerTimes.fajr.toEpochMilliseconds()))
            val sunriseTimee =
                formatter.format(Date(prayerTimes.sunrise.toEpochMilliseconds()))
            val dhuhrTime =
                formatter.format(Date(prayerTimes.dhuhr.toEpochMilliseconds()))
            val asrTimee =
                formatter.format(Date(prayerTimes.asr.toEpochMilliseconds()))
            val maghribTimee =
                formatter.format(Date(prayerTimes.maghrib.toEpochMilliseconds()))
            val ishaTime =
                formatter.format(Date(prayerTimes.isha.toEpochMilliseconds()))
//            ========= End of Formatter and each pray time =======
//            ========= Next pray name ===========
            val now = Clock.System.now()
            val currentPrayer = prayerTimes.currentPrayer(now)
            val nextPrayer = if (currentPrayer == Prayer.ISHA){
                Prayer.FAJR }else{
                prayerTimes.nextPrayer(now)
            }
//             ========= End of next pray name ===========
//             ========= Next pray time ===========

            var nextPrayerTime =prayerTimes.timeForPrayer(nextPrayer)
            if (currentPrayer == Prayer.ISHA) {
                day = day.inc()
                date = DateComponents(year, month, day)
                prayerTimes = PrayerTimes(coordinates, date, params)
                nextPrayerTime = prayerTimes.timeForPrayer(nextPrayer)
                fajrTimee =
                    formatter.format(Date(prayerTimes.fajr.toEpochMilliseconds()))
            }
//             ========= End of next pray time ===========

            val nextPrayerTimeS =
                formatter.format((nextPrayerTime?.toEpochMilliseconds())) // format for the next pray time as string
//            ========== Countdown for the next pray ===========
            val diff = nextPrayerTime?.toEpochMilliseconds()!!.minus(now.toEpochMilliseconds())
            val countDownTimer = object : CountDownTimer(diff, 1000) {
                override fun onTick(millisUntilFinished: Long) {

                    val hours = millisUntilFinished / 3600000
                    val minutes = (millisUntilFinished % 3600000) / 60000
                    val seconds = (millisUntilFinished % 60000) / 1000
                    val remainingTime = "-$hours:$minutes:$seconds"
                    binding.countdown.text=remainingTime
                }

                override fun onFinish() {
                    binding.countdown.text = getString(R.string.its) +"$nextPrayer"+getString(R.string.time)
                }
            }
            countDownTimer.start()

//            ========== End of countdown for the next pray ===========

//                ====== date formatter ==========
//            == Gregorian ==
            val dateGregorian = LocalDate.now()
            val patternGregorian = "EEEE. dd MMMM"
            val formatterGregorian = DateTimeFormatter.ofPattern(patternGregorian)
            val formattedGregorianDate = dateGregorian.format(formatterGregorian)
////            == Hijri ==
            val hijrahDate = HijrahDate.now()
            val patternHijrah = "d MMMM yyyy 'H'"
            val formatterHijrah = DateTimeFormatter.ofPattern(patternHijrah)
            val formattedHijriDate = hijrahDate.format(formatterHijrah)
//            ========== End date formatter  =======
            binding.apply {
                locationText.text = placeName
                fajrTime.text = fajrTimee
                sunriseTime.text = sunriseTimee
                zohrTime.text = dhuhrTime
                asrTime.text = asrTimee
                maghribTime.text = maghribTimee
                eshaaTime.text = ishaTime
                birthCalender.text= formattedGregorianDate
                higriCalender.text=formattedHijriDate
                prayName.text = nextPrayer.toString()
                prayTime.text = nextPrayerTimeS
            }
        }

//        ================================ end of prayer time handling ==================

        return binding.root
    }
    private fun requestLocationPermission(){
        ActivityCompat.requestPermissions(requireActivity(),
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION ,
                Manifest.permission.ACCESS_COARSE_LOCATION),
            locationRequestCode
        )
    }
    private fun checkSelfPermission():Boolean{
        return ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    }

    override fun onDestroy() {
        super.onDestroy()

        locationManager.removeUpdates(this)
    }

    override fun onLocationChanged(location: Location) {

    }
}