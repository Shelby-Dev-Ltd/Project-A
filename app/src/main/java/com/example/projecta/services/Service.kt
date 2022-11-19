package com.example.projecta.services

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.example.projecta.Constants
import com.example.projecta.MainActivity
import com.example.projecta.R
import java.util.*


class Service : Service() {

    private var c = Constants
    private var isRunning:Boolean = false
    lateinit var notificationManager: NotificationManager
    lateinit var notificationBuilder: NotificationCompat.Builder
    lateinit var handler: Handler
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(init: Intent, flag: Int, startId: Int): Int {

        if(!isRunning){
            startForegroundService()
            isRunning = true
        } else{
            Toast.makeText(applicationContext, "Service is already running..", Toast.LENGTH_SHORT).show()
        }
        return super.onStartCommand(init, flag, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null

    }

private fun startForegroundService(){
    handler = Handler()
    var runnable: Runnable? = null
    var delay = 1000
  

    notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
            as NotificationManager

    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
        createNotificationChannel(notificationManager)
    }

    notificationBuilder = NotificationCompat.Builder(this, c.NOTIFICATION_CHANNEL_ID )
        .setAutoCancel(false)
        .setOngoing(true)
        .setSmallIcon(R.drawable.projecta_logo_crop)
        .setContentTitle("Running App")
        .setContentText("TIME")
        .setContentIntent(getMainActivityPendingIntent())

    startForeground(c.NOTIFICATION_ID, notificationBuilder.build()  )

    handler.postDelayed(Runnable {
        handler.postDelayed(runnable!!, delay.toLong())
        notificationBuilder.setContentText(timeStringFromLong(CURR_TIME))
        notificationManager.notify(c.NOTIFICATION_ID, notificationBuilder.build())
    }.also { runnable = it }, delay.toLong())


}

    private fun getMainActivityPendingIntent() = PendingIntent.getActivity(
        this,
        0,
        Intent(this, MainActivity::class.java).also{
            it.action = c.ACTION_SHOW_HOME_FRAGMENT
        },
        FLAG_UPDATE_CURRENT
    )

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            c.NOTIFICATION_CHANNEL_ID,
            c.NOTIFICATION_CHANNEL_NAME,
            IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }

    fun timeStringFromLong(ms: Long): String {
        val seconds = (ms / 1000) % 60
        val minutes = (ms / (1000 * 60) % 60)
        val hours  = (ms / (1000 * 60 * 60) % 24)
        return makeTimeString (hours, minutes, seconds)
    }

    private fun makeTimeString(hours: Long, minutes: Long, seconds: Long): String {

        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    companion object{
        var CURR_TIME:Long = Date().time
        var x:Long = Date().time
        var address:String = "Address template"
    }

}