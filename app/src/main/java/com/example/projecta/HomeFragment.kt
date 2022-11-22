package com.example.projecta

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import android.telephony.SmsManager
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.motion.widget.Debug.getLocation
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import com.example.projecta.databinding.FragmentHomeBinding
import com.example.projecta.services.Service
import com.example.projecta.services.Service.Companion.address
import com.example.projecta.services.Service.Companion.appAct
import com.example.projecta.services.Service.Companion.called
import com.example.projecta.services.Service.Companion.isThere
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.io.IOException
import java.util.*


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var appContext: Context
    private val timer = Timer()
    private lateinit var currActivity: Activity
    private val permissionRequest = 101
    private lateinit var auth: FirebaseAuth

    //STOPWATCH
    private lateinit var dataHelper: DataHelper
    var isTimeTask = false
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

        currActivity = requireActivity()
        appContext = context?.applicationContext!!
        dataHelper = DataHelper(appContext)
        appAct = requireActivity()



        startAction()

        val handler = Handler()
        handler.postDelayed(Runnable { // Do something after 5s = 5000ms
            getLocation()
        }, 500)

        if (!isTimeTask) {
            timer.scheduleAtFixedRate(TimeTask(), 0, 500)
            isTimeTask = true
        }

//        var button = binding.btn
//        var button2 = binding.btn2
////        var button3 = binding.back
//        var address = binding.address

        t1 = TextToSpeech(appContext,
            OnInitListener { i ->
                if (i != TextToSpeech.ERROR) t1?.setLanguage(
                    Locale(
                        "id",
                        "ID"
                    )
                )
            })

        binding.btnAlert.setOnClickListener {
            if (!dataHelper.timerCounting()) {
                Service.x = Date().time
                startStopAction()
            } else {
                startStopAction()
            }
        }

        //Initialize fusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(appContext)

//        button.setOnClickListener(View.OnClickListener { //Check permission
//
//            if (
//                ActivityCompat.checkSelfPermission(
//                    appContext,
//                    Manifest.permission.ACCESS_FINE_LOCATION
//                ) == PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(
//                    appContext,
//                    Manifest.permission.ACCESS_COARSE_LOCATION
//                ) == PackageManager.PERMISSION_GRANTED
//            ) {
//                getLocation()
//            } else {
//                ActivityCompat.requestPermissions(
//                    requireActivity(),
//                    arrayOf(
//                        Manifest.permission.ACCESS_FINE_LOCATION,
//                        Manifest.permission.ACCESS_COARSE_LOCATION
//                    ),
//                    100
//                )
//            }
//        })
//
//
//
//        button2.setOnClickListener(View.OnClickListener {
//            val text: String = address.getText().toString()
//            t1!!.speak(text, TextToSpeech.QUEUE_FLUSH, null)
//        })

//        button3.setOnClickListener(View.OnClickListener {
//            startActivity(
//                Intent(
//                    requireActivity(),
//                    MainActivity::class.java
//                )
//            )
//        })


        //STOPWATCH


        if (dataHelper.timerCounting()) {
            startTimer()
        } else {
            stopTimer()
            if (dataHelper.startTime() != null && dataHelper.stopTime() != null) {
                val time = Date().time - calcRestartTime().time
                binding.btnAlert.text = timeStringFromLong(time)

            }
        }


    }

    private inner class TimeTask : TimerTask() {
        override fun run() {
            if (dataHelper.timerCounting()) {
                val time = Date().time - Service.x
                Service.CURR_TIME = time
                isThere = false
                val interval = 5000

                currActivity.runOnUiThread(java.lang.Runnable {
                    binding.btnAlert.text = timeStringFromLong(time)
                    if (time.toInt() >= interval && dataHelper.timerCounting()) {
                        stopTimer()

                        AlertDialog.Builder(requireContext())
                            .setMessage("Are you still there??")
                            .setPositiveButton(
                                "Yes",
                                DialogInterface.OnClickListener { dialogInterface, i ->
                                    Service.x = Date().time
                                    startTimer()
                                    isThere = true
                                    called = false
                                })
                            .create()
                            .show()


                        val handler = Handler()
                        handler.postDelayed(Runnable { // Do something after 5s = 5000ms
                            if (!isThere) {
                                Toast.makeText(appContext, "Calling", Toast.LENGTH_SHORT).show()
                            }
                        }, 5000)
                    }


                })
            }
        }
    }

    private fun startAction() {
        if (!dataHelper.timerCounting()) {
            startTimer()
        }
    }

    private fun resetAction() {
        dataHelper.setStopTime(null)
        dataHelper.setStartTime(null)
        stopTimer()
        binding.btnAlert.text = timeStringFromLong(0)
    }

    fun timeStringFromLong(ms: Long): String {
        val seconds = (ms / 1000) % 60
        val minutes = (ms / (1000 * 60) % 60)
        val hours = (ms / (1000 * 60 * 60) % 24)
        return makeTimeString(hours, minutes, seconds)
    }

    private fun makeTimeString(hours: Long, minutes: Long, seconds: Long): String {

        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    private fun stopTimer() {
        dataHelper.setTimerCounting(false)
    }

    private fun startTimer() {
        dataHelper.setTimerCounting(true)
    }


    private fun startStopAction() {
        if (dataHelper.timerCounting()) {
            dataHelper.setStopTime(Date())
            stopTimer()
        } else {
            if (dataHelper.stopTime() != null) {
                dataHelper.setStartTime(calcRestartTime())
                dataHelper.setStopTime(null)
            } else {
                dataHelper.setStartTime(Date())
            }
            startTimer()
        }
    }

    private fun calcRestartTime(): Date {
        val diff = dataHelper.startTime()!!.time - dataHelper.stopTime()!!.time
        return Date(System.currentTimeMillis() + diff)
    }


    //LOCATION

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
                    val location: Location? = task.result
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
                            address = addresses[0].getAddressLine(0)

//                            //Set latitude on TextView
//                            binding.latitude.setText("Latitude: " + addresses[0].latitude)
//                            //Set longitude on TextView
//                            binding.longitude.setText("Longitude: " + addresses[0].longitude)
//                            //Set country on TextView
//                            binding.country.setText("Country: " + addresses[0].countryName)
//                            //Set city on TextView
//                            binding.city.setText("City: " + addresses[0].locality)
//                            //Set address on TextView
//                            binding.address.setText("Address: " + addresses[0].getAddressLine(0))
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
            })
    }


}


