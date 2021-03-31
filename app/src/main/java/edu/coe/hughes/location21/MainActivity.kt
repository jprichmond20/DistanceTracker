package edu.coe.hughes.location21

import android.Manifest
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.IOException
import java.util.*


class MainActivity : AppCompatActivity() {
    val LogTag = "GPS"
    private val REQUEST_LOCATION = 123
    var locationManager: LocationManager? = null
    var providers: List<String>? = null
    var preferred: String = LocationManager.GPS_PROVIDER

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
            !== PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), PackageManager.PERMISSION_GRANTED)
        }

        setContentView(R.layout.activity_main)
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        for (prov:String in locationManager!!.allProviders){
            Log.i(LogTag, "Provider:  " + prov)
        }

        val criteria = Criteria()
        criteria.accuracy = Criteria.ACCURACY_FINE
         providers= locationManager!!.getProviders(criteria, true);

        if (providers == null || providers!!.size == 0) {
            Log.e(LogTag, "cannot_get_gps_service")
            Toast.makeText(
                this, "Could not open GPS service",
                Toast.LENGTH_LONG
            ).show()
            return
        }
        preferred = providers!![0] // first == preferred
    }

    object Tracker {
        var totDistance: Long = 0L
    }
    override fun onResume() {
        super.onResume()
        Log.i(LogTag, "onResume")

        val provider = preferred

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
            !== PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION ), PackageManager.PERMISSION_GRANTED)
        }
        else {

            locationManager!!.requestLocationUpdates(
                provider, 2000, 10f,
                locationListener
            )
            val location = locationManager!!.getLastKnownLocation(provider)
            updateWithNewLocation(location)
        }
    }

    override fun onPause() {
        super.onPause()
        locationManager!!.removeUpdates(locationListener)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_LOCATION -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this@MainActivity, "Location Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@MainActivity, "Location Permission Denied", Toast.LENGTH_SHORT).show()
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions!!, grantResults)
        }
    }

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            updateWithNewLocation(location)
        }

        override fun onProviderDisabled(provider: String) {
            updateWithNewLocation(null)
        }

        override fun onProviderEnabled(provider: String) {}
        override fun onStatusChanged(
            provider: String, status: Int,
            extras: Bundle
        ) {
        }
    }

    private fun updateWithNewLocation(location: Location?) {
        var disBetween = FloatArray(12)
        val formerLat = Prefs.loadLat(this)
        val formerLong = Prefs.loadLong(this)
        val formerLocation: Location
        Log.i("LATLONG", formerLat + ", " + formerLong)
        val latLongString: String
        val myLocationText: TextView
        myLocationText = findViewById<View>(R.id.myLocationText) as TextView
        var addressString = "No address found"
        if (location != null) {
            val lat = location.latitude
            val lng = location.longitude
            latLongString = "Lat:$lat\nLong:$lng"
            val latitude = location.latitude
            val longitude = location.longitude
            Location.distanceBetween(lat, lng, formerLat!!.toDouble(), formerLong!!.toDouble(), disBetween)
            Prefs.savePref(this, latitude, longitude)
            val gc = Geocoder(this, Locale.getDefault())
            try {
                val addresses = gc.getFromLocation(latitude, longitude, 1)
                val sb = StringBuilder()
                if (addresses.size > 0) {
                    val address = addresses[0]
                    sb.append(address.getAddressLine(0)).append("\n")
                    sb.append(address.featureName).append("\n")
                    sb.append(address.locality).append("\n")
                    sb.append(address.postalCode).append("\n")
                    sb.append(address.countryName)
                }
                addressString = sb.toString()
                Prefs.savePref(this, latitude, longitude)
                Log.i(LogTag,addresses.toString())
            } catch (e: IOException) {
            }
        } else {
            latLongString = "No location found"
        }


        myLocationText.text = "Your Current Position is:\n$latLongString\n$addressString"
    }
}