package com.mkd.whatstheweather.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mkd.whatstheweather.BuildConfig
import com.mkd.whatstheweather.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    //Binding
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    //onCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}