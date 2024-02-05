package com.demo1.prayerlight

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowCompat
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
            if (checkSelfPermission() && checkLocationSetting() && internetConnection()){
                val intent = Intent(this,MainActivity::class.java)
                startActivity(intent)
            }else if (!checkLocationSetting()) {
                requestLocationSettings()
            }
            else if (!internetConnection()){
                internetDialogue()
            }
            else{
                Toast.makeText(
                    this,
                    getString(R.string.location_permission_msg),
                    Toast.LENGTH_SHORT
                ).show()
                requestLocationPermission()
            }
        }
        binding.location.setOnClickListener{
            if (!checkLocationSetting()) {
                requestLocationSettings()
            }else if (!checkSelfPermission()){
                Toast.makeText(
                    this,
                    getString(R.string.location_permission_msg),
                    Toast.LENGTH_SHORT
                ).show()
                requestLocationPermission()
            }else{
                Toast.makeText(this,getString(R.string.location_configuration_finished),Toast.LENGTH_SHORT).show()
            }
        }
        WindowCompat.setDecorFitsSystemWindows(window, false)

    }

////    ========= Request location permission =======
private fun requestLocationSettings(){
    val locationRequest = LocationRequest.Builder(10).
    setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY).build()
    val builder = LocationSettingsRequest.Builder()
        .addLocationRequest(locationRequest)
    val settingsClient = LocationServices.getSettingsClient(this)
    val task = settingsClient.checkLocationSettings(builder.build())
    task.addOnSuccessListener {
            requestLocationPermission()
    }
    task.addOnFailureListener { exception ->
        if (exception is ResolvableApiException){
            try {
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

//        ====== Network status ================
    private fun internetConnection():Boolean{
        val connectivityManager = getSystemService(ConnectivityManager::class.java)
        val currentNetwork = connectivityManager.activeNetwork
        val caps = connectivityManager.getNetworkCapabilities(currentNetwork)
        val validation =caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)?:false
        val internet = caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)?:false
        return validation && internet
    }
    private fun internetDialogue(){
        val alertDialog = AlertDialog.Builder(this,R.style.Dialog_Style)
            .setTitle(getString(R.string.no_internet))
            .setMessage(getString(R.string.check_internet))
            .setPositiveButton(getString(R.string.settings)) { _, _ ->
                val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(intent)
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        alertDialog.show()
    }
}