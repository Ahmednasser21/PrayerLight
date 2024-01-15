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
import com.demo1.prayerlight.databinding.FragmentMainBinding
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Main.newInstance] factory method to
 * create an instance of this fragment.
 */
class Main : Fragment(), LocationListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentMainBinding
    private lateinit var locationManager: LocationManager
    private var location: Location? = null
    private val locationRequestCode = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)

 //        ===== Get last known location and ask for permission if it is not granted====

            location = when (checkSelfPermission()) {
            true -> {
                // Permission is already granted, get the last known location
                locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            }
            false -> {
                requestLocationPermission()

                if(checkSelfPermission())locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

                else  Toast.makeText(requireContext(), getString(R.string.location_permission_msg), Toast.LENGTH_SHORT).show()
                null
            }
        }

        if (location != null) {
            val longitude = location!!.longitude
            val latitude = location!!.latitude
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            val addresses: List<Address> =
            geocoder.getFromLocation(latitude, longitude, 1) as List<Address>
            val placeName = addresses[0].getAddressLine(0)
            binding.locationText.text = placeName
        }
//        ================================ prayer time handling ==================
//            val coordinates = Coordinates(location!!.latitude, location!!.longitude)
//
//            val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
//            val month = Calendar.getInstance().get(Calendar.MONTH)
//            val year = Calendar.getInstance().get(Calendar.YEAR)
//
//            val date = DateComponents(year, month, day)
//            val params = CalculationMethod.EGYPTIAN.parameters
//                .copy(madhab = Madhab.HANAFI, prayerAdjustments = PrayerAdjustments(fajr = 19))
//
//            val prayerTimes = PrayerTimes(coordinates, date, params)
//
//
//            val fajrTime = prayerTimes.fajr
//            val sunriseTime = prayerTimes.sunrise
//            val dhuhrTime = prayerTimes.dhuhr
//            val asrTime = prayerTimes.asr
//            val maghribTime = prayerTimes.maghrib
//            val ishaTime = prayerTimes.isha
//
////            binding.fajrTime.text = fajrTime.toString()
////            binding.sunriseTime.text = sunriseTime.toString()
////            binding.zohrTime.text = dhuhrTime.toString()
////            binding.asrTime.text = asrTime.toString()
////            binding.maghribTime.text = maghribTime.toString()
////            binding.eshaaTime.text = ishaTime.toString()
////            ========================================================
//
////               binding.prayName.text = prayerTimes.currentPrayer(fajrTime).toString()
////                binding.prayTime.text = prayerTimes.nextPrayer(dhuhrTime).toString()
////            binding.countdown.text=prayerTimes.timeForPrayer(Prayer.SUNRISE).toString()
//
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

        val longitude = location.longitude
        val latitude = location.latitude
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        val addresses: List<Address> = geocoder.getFromLocation(latitude, longitude, 1) as List<Address>
        val placeName = addresses[0].getAddressLine(0)
        binding.locationText.text =placeName
    }




    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Main.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Main().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}

//        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//
//            if (checkSelfPermission()) requestLocationPermission()
//
//           locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)
//
//        }