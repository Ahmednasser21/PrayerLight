package com.demo1.prayerlight

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.demo1.prayerlight.databinding.ActivityLocationBinding
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority

class LocationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLocationBinding
    private val locationPermissionRequestCode = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.previous.setOnClickListener{
            val intent = Intent(this,AlarmSettings::class.java)
            startActivity(intent)
        }
        binding.finish.setOnClickListener{
            if (checkSelfPermission() && checkLocationSetting()){
                val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            }else if(!checkSelfPermission()) {
                Toast.makeText(this , getString(R.string.location_permission_msg),Toast.LENGTH_SHORT).show()
                requestLocationPermission()
            }
            else {
                Toast.makeText(this,getString(R.string.location_setting_msg),Toast.LENGTH_LONG).show()
                requestLocationSettings()
            }
        }
        binding.location.setOnClickListener{
            Toast.makeText(this , getString(R.string.location_permission_msg),Toast.LENGTH_SHORT).show()
            requestLocationPermission()
        }
    }

////    ========= Request location permission =======
private fun requestLocationSettings(){
    val locationRequest = LocationRequest.Builder(1000).
    setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY).build()
    val builder = LocationSettingsRequest.Builder()
        .addLocationRequest(locationRequest)
    val settingsClient = LocationServices.getSettingsClient(this)
    val task = settingsClient.checkLocationSettings(builder.build())
    task.addOnSuccessListener {
        // All location settings are satisfied, start the next activity
    }
    task.addOnFailureListener { exception ->
        if (exception is ResolvableApiException){
            // Location settings are not satisfied, show the user a dialog
            try {
                // Show the dialog by calling startResolutionForResult(),
                // and check the result in onActivityResult().
                exception.startResolutionForResult(this, locationPermissionRequestCode)
            } catch (sendEx: IntentSender.SendIntentException) {
                // Ignore the error.
            }
        }
    }
}
    private fun requestLocationPermission(){
        ActivityCompat.requestPermissions(this ,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION)
            ,locationPermissionRequestCode)
    }
    private fun checkSelfPermission():Boolean{

        return   ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)==
                PackageManager.PERMISSION_GRANTED
    }
    private fun checkLocationSetting():Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        return isGpsEnabled && isNetworkEnabled

    }
}