package com.example.projecta

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.projecta.databinding.ActivityUserprofileBinding

class userprofile : AppCompatActivity() {
    lateinit var binding : ActivityUserprofileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.title.text = "Activity User Profile"
    }
}