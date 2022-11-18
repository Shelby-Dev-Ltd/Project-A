package com.example.projecta

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.Fragment
import com.example.projecta.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.*
import com.example.projecta.services.Service

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var toggle: ActionBarDrawerToggle

    lateinit var dataHelper: DataHelper
    private lateinit var auth: FirebaseAuth


    private var x = Date().time
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dataHelper = DataHelper(applicationContext)


        //Services

        val intent = Intent(this@MainActivity, Service::class.java) // Build the intent for the service
        applicationContext.startForegroundService(intent)








        // Initialize Firebase Auth
        auth = Firebase.auth



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