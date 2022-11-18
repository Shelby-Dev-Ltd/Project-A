package com.example.projecta.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.projecta.MainActivity
import com.example.projecta.R
import com.example.projecta.Constants

class Service : Service() {

    private var c = Constants
    private var isRunning:Boolean = false

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
    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
            as NotificationManager

    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
        createNotificationChannel(notificationManager)
    }

    val notificationBuilder = NotificationCompat.Builder(this, c.NOTIFICATION_CHANNEL_ID )
        .setAutoCancel(false)
        .setOngoing(true)
        .setSmallIcon(R.drawable.projecta_logo_crop)
        .setContentTitle("Running App")
        .setContentText("00:00:00")
        .setContentIntent(getMainActivityPendingIntent())

    startForeground(c.NOTIFICATION_ID, notificationBuilder.build()  )
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
}