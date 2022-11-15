package com.example.projecta

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.*

class MainActivity2 : AppCompatActivity(), SensorEventListener, TextToSpeech.OnInitListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var square: TextView
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val permissionId = 2
    private lateinit var tts: TextToSpeech
    private lateinit var latitude: TextView
    private lateinit var longitude: TextView
    private lateinit var country: TextView
    private lateinit var city: TextView
    private lateinit var address: TextView
    private lateinit var btnSpeak: Button
    private lateinit var button: Button
    private lateinit var btnLocation: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        button = findViewById(R.id.back)
        btnLocation = findViewById(R.id.btn)
        btnSpeak = findViewById(R.id.btn2)
        latitude = findViewById(R.id.latitude)
        longitude = findViewById(R.id.longitude)
        country = findViewById(R.id.country)
        city = findViewById(R.id.city)
        address = findViewById(R.id.address)


        btnSpeak!!.isEnabled = false

        // TextToSpeech(Context: this, OnInitListener: this)
        tts = TextToSpeech(this, this)

        btnSpeak!!.setOnClickListener { speakOut() }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        btnLocation.setOnClickListener {
            getLocation()
        }

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        square = findViewById(R.id.square);

        setUpSensorStuff()

        button.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        sensorManager.unregisterListener(this)
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroy()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts!!.setLanguage(Locale("id", "ID"))

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS","The Language not supported!")
            } else {
                btnSpeak!!.isEnabled = true
            }
        }
    }
    private fun speakOut() {
        val text = address!!.text.toString()
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null,"")
    }


    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        val geocoder = Geocoder(this, Locale.getDefault())
                        val list: List<Address> = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        apply {
                            latitude.text = "Latitude: \n${list[0].latitude}"
                            longitude.text = "Longitude: \n${list[0].longitude}"
                            country.text = "Country: \n${list[0].countryName}"
                            city.text = "City: \n${list[0].locality}"
                            address.text = "Address: \n${list[0].getAddressLine(0)}"
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Please turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }
    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }
    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            permissionId
        )
    }
    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
    }


    private fun setUpSensorStuff() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_FASTEST, SensorManager.SENSOR_DELAY_FASTEST)
        }
    }

    override fun onSensorChanged(p0: SensorEvent?) {
        if (p0?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val sides = p0.values[0]
            val upDown = p0.values[1]

            square.apply {
                rotationX = upDown * 3f
                rotationY = sides * 3f
                rotation = -sides
                translationX = sides * -10
                translationY = upDown * 10
            }

            val color = if (upDown.toInt() == 0 && sides.toInt() == 0) Color.GREEN else Color.RED
            square.setBackgroundColor(color)

            square.text = "up/down ${upDown.toInt()}\nleft/right ${sides.toInt()}"
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        return
    }

}




