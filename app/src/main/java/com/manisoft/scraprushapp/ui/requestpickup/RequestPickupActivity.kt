package com.manisoft.scraprushapp.ui.requestpickup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.manisoft.scraprushapp.databinding.ActivityRequestPickupBinding

class RequestPickupActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRequestPickupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRequestPickupBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}