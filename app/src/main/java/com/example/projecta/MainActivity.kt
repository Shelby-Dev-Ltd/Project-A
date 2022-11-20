package com.example.projecta

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.projecta.databinding.ActivityMainBinding
import com.example.projecta.services.Service
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.*


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var toggle: ActionBarDrawerToggle

    lateinit var dataHelper: DataHelper
    private lateinit var auth: FirebaseAuth
    var PERMISSION_ALL = 1
    var PERMISSIONS =
        arrayOf<String>(Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE, Manifest.permission.SEND_SMS, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)

    private var x = Date().time
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {


        //request telephone permission
        if(!hasPermissions(this, *PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
        Service.appAct = this


        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dataHelper = DataHelper(applicationContext)


        //Services

        val intent = Intent(this@MainActivity, Service::class.java) // Build the intent for the service
        applicationContext.startForegroundService(intent)








        // Initialize Firebase Auth
        auth = Firebase.auth
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            Toast.makeText(this, "User authenticated!", Toast.LENGTH_SHORT)
        } else{
            replaceFragment(LoginFragment(), "Login")
            return
        }



        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open, R.string.close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            supportActionBar?.hide()
            replaceFragment(LoginFragment(), "Login")
        } else{
            supportActionBar?.show()
            replaceFragment(HomeFragment(), "Home")
        }
        binding.navView.setNavigationItemSelectedListener {
            it.isChecked = true
            replaceFragment(HomeFragment(), it.title.toString())
            when (it.itemId) {
                R.id.firstItem -> replaceFragment(HomeFragment(), it.title.toString())
                R.id.secondItem -> replaceFragment(UserProfileFragment(), it.title.toString())
                R.id.thirdItem -> {
                    FirebaseAuth.getInstance().signOut()
                    replaceFragment(LoginFragment(), it.title.toString())
                }
            }
            true
        }

    }

    fun hasPermissions(context: Context?, vararg permissions: String?): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        permission!!
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }
            }
        }
        return true
    }

    private fun replaceFragment(fragment: Fragment, title: String) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
        binding.drawerLayout.closeDrawers()
        setTitle(title)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        } else {
            return super.onOptionsItemSelected(item)
        }
    }
    private fun startTimer() {
        dataHelper.setTimerCounting(true)
    }
    private fun startAction() {
        if(!dataHelper.timerCounting()){
            startTimer()
        }
    }

}