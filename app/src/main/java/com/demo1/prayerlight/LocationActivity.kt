package com.demo1.prayerlight

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.demo1.prayerlight.databinding.ActivityLocationBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.util.Locale

class LocationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLocationBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private val locationPermissionRequestCode = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.next.setOnClickListener{
            if (checkSelfPermission()){
                val intent = Intent(this,AlarmSettings::class.java)
                startActivity(intent)
            }else{
                Toast.makeText(this,getString(R.string.location_permission_msg),Toast.LENGTH_LONG).show()
                requestLocationPermission()
            }
        }
        binding.previous.setOnClickListener{
            val intent = Intent(this,LanguageActivity::class.java)
            startActivity(intent)
        }
//        ============ location ==============
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.Builder(1000L)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .build()
        locationCallback = object : LocationCallback(){
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val location = locationResult.lastLocation
                val longitude = location?.longitude
                val latitude = location?.latitude
            }
        }



        binding.location.setOnClickListener{

            startLocationUpdates()
        }
        val task = fusedLocationProviderClient.getCurrentLocation (locationRequest.priority, null)
        task.addOnSuccessListener { location ->
            if (location != null) {
                val longitude = location.longitude
                val latitude = location.latitude
                val geocoder = Geocoder(this, Locale.getDefault())
                val addresses: List<Address> = geocoder.getFromLocation(latitude, longitude, 1) as List<Address>
                val placeName = addresses[0].getAddressLine(0)
                binding.txt2.text = placeName.toString()
            }
        }
    }
    override fun onResume () {
        super.onResume ()

        if (checkSelfPermission ()) startLocationUpdates ()
    }

    override fun onPause () {
        super.onPause ()

        stopLocationUpdates ()
    }
    //    ========= location handling ===============
//    ========= Request location permission =======
    private fun requestLocationPermission(){
        ActivityCompat.requestPermissions(this ,
            arrayOf( Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION),
            locationPermissionRequestCode)
    }
    //     ================= check permission ===========
    private fun checkSelfPermission():Boolean{
        return ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)==
                PackageManager.PERMISSION_GRANTED
    }

    private fun startLocationUpdates() {
        if (checkSelfPermission()) {
            fusedLocationProviderClient.requestLocationUpdates (locationRequest, locationCallback, null)

        }else{
            requestLocationPermission()
        }

    }
    private fun stopLocationUpdates () {
        fusedLocationProviderClient.removeLocationUpdates (locationCallback)
    }

    // ================ OnRequestPermissionsResult ================
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionRequestCode){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED ){
                startLocationUpdates()
            }
        }else{
            Toast.makeText(this,getString(R.string.location_permission_msg),Toast.LENGTH_LONG).show()
            requestLocationPermission()
        }
    }
//     ============ End of location handling ===============
}