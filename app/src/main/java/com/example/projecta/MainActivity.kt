package com.example.projecta

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import com.example.projecta.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var toggle: ActionBarDrawerToggle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply{
            toggle = ActionBarDrawerToggle(this@MainActivity, drawerLayout, R.string.open, R.string.close)
            drawerLayout.addDrawerListener(toggle)
            toggle.syncState()

            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            navView.setNavigationItemSelectedListener {
                when(it.itemId){
                    R.id.firstItem->{
                        Toast.makeText(this@MainActivity, "Home Page", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@MainActivity, MainActivity::class.java))
                    }
                    R.id.secondItem->{
                        Toast.makeText(this@MainActivity, "User Profile Page", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@MainActivity, userprofile::class.java))
                    }
                    R.id.thirdItem->{
                        Toast.makeText(this@MainActivity, "Ti", Toast.LENGTH_SHORT).show()
                    }
                }
                true
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            true
        }
        return super.onOptionsItemSelected(item)
    }
}