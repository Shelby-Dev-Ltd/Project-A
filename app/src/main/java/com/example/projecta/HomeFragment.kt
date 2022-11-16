package com.example.projecta

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.projecta.databinding.FragmentHomeBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import java.io.IOException
import java.util.*


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var appContext: Context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }


    var fusedLocationProviderClient: FusedLocationProviderClient? = null
    var t1: TextToSpeech? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appContext = context?.applicationContext!!

        var button = binding.btn
        var button2 = binding.btn2
//        var button3 = binding.back
        var address = binding.address

        t1 = TextToSpeech(appContext,
            OnInitListener { i -> if (i != TextToSpeech.ERROR) t1?.setLanguage(Locale("id", "ID")) })

        //Initialize fusedLocationProviderClient

        //Initialize fusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(appContext)

        button.setOnClickListener(View.OnClickListener { //Check permission

            if (
                    ActivityCompat.checkSelfPermission(
                        appContext
                        ,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                    appContext,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                getLocation()
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    100
                )
            }
        })

        button2.setOnClickListener(View.OnClickListener {
            val text: String = address.getText().toString()
            t1!!.speak(text, TextToSpeech.QUEUE_FLUSH, null)
        })

//        button3.setOnClickListener(View.OnClickListener {
//            startActivity(
//                Intent(
//                    requireActivity(),
//                    MainActivity::class.java
//                )
//            )
//        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //Check condition
        if (requestCode == 100 && grantResults.size > 0 && (grantResults[0] + grantResults[1]
                    == PackageManager.PERMISSION_GRANTED)
        ) {
            getLocation()
        } else {
            Toast.makeText(appContext, "Permission denied.", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        fusedLocationProviderClient?.getLastLocation()
            ?.addOnCompleteListener(object : OnCompleteListener<Location?> {
                override fun onComplete(task: Task<Location?>) {
                    //Initialize location
                    val location: Location? = task.getResult()
                    if (location != null) {
                        try {
                            //Initialize geoCoder
                            val geocoder = Geocoder(
                                appContext,
                                Locale.getDefault()
                            )
                            //Initialize address list
                            val addresses = geocoder.getFromLocation(
                                location.latitude, location.longitude, 1
                            )
                            //Set latitude on TextView
                            binding.latitude.setText("Latitude: " + addresses[0].latitude)
                            //Set longitude on TextView
                            binding.longitude.setText("Longitude: " + addresses[0].longitude)
                            //Set country on TextView
                            binding.country.setText("Country: " + addresses[0].countryName)
                            //Set city on TextView
                            binding.city.setText("City: " + addresses[0].locality)
                            //Set address on TextView
                            binding.address.setText("Address: " + addresses[0].getAddressLine(0))
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
            })
    }
}