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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.batoulapps.adhan2.CalculationMethod
import com.batoulapps.adhan2.Coordinates
import com.batoulapps.adhan2.Madhab
import com.batoulapps.adhan2.PrayerAdjustments
import com.batoulapps.adhan2.PrayerTimes
import com.batoulapps.adhan2.data.DateComponents
import com.demo1.prayerlight.databinding.FragmentMainBinding
import java.text.SimpleDateFormat
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


//        viewLifecycleOwner.lifecycleScope.launch {
//            getData()
//        }
        location?.let { loc ->
            val longitude = loc.longitude
            val latitude = loc.latitude
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            val addresses: List<Address> =
                geocoder.getFromLocation(latitude, longitude, 1) as List<Address>
            val cityName = addresses[0].subAdminArea
            val governmentName = addresses[0].adminArea
            val countryName = addresses[0].countryName
            val placeName = "$cityName, $governmentName, $countryName"
            val coordinates = Coordinates(latitude, longitude)
            val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            val month = Calendar.getInstance().get(Calendar.MONTH)+1
            val year = Calendar.getInstance().get(Calendar.YEAR)
            val date = DateComponents(year, month, day)
            val params = CalculationMethod.EGYPTIAN.parameters
                .copy(
                    madhab = Madhab.SHAFI, prayerAdjustments =
                    PrayerAdjustments(
                        fajr = 7, dhuhr = -1,
                        asr = -1, isha = -2
                    )
                )
            val prayerTimes = PrayerTimes(coordinates, date, params)

            val formatter = SimpleDateFormat("hh:mm a",Locale.getDefault())
            formatter.timeZone = TimeZone.getTimeZone("Africa/Cairo")
            val fajrTimee =
                formatter.format(Date(prayerTimes.fajr.toEpochMilliseconds())).toString()
            val sunriseTimee =
                formatter.format(Date(prayerTimes.sunrise.toEpochMilliseconds())).toString()
            val dhuhrTime =
                formatter.format(Date(prayerTimes.dhuhr.toEpochMilliseconds())).toString()
            val asrTimee =
                formatter.format(Date(prayerTimes.asr.toEpochMilliseconds())).toString()
            val maghribTimee =
                formatter.format(Date(prayerTimes.maghrib.toEpochMilliseconds())).toString()
            val ishaTime =
                formatter.format(Date(prayerTimes.isha.toEpochMilliseconds())).toString()

            binding.apply {
                locationText.text = placeName
                fajrTime.text = fajrTimee
                sunriseTime.text = sunriseTimee
                zohrTime.text = dhuhrTime
                asrTime.text = asrTimee
                maghribTime.text = maghribTimee
                eshaaTime.text = ishaTime
            }


        }
////            ========================================================
//        val now = Clock.System.now()
//        val currentPrayer = prayerTimes.currentPrayer(now)
//        val nextPrayer = prayerTimes.nextPrayer(now)
//        val countdown =
//            formatter.format(prayerTimes.timeForPrayer(nextPrayer)?.toEpochMilliseconds())
//        binding.prayName.text = currentPrayer.toString()
//        binding.prayTime.text = nextPrayer.toString()
//        binding.countdown.text = countdown.toString()

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

//        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//
//            if (checkSelfPermission()) requestLocationPermission()
//
//           locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)
//
//        }