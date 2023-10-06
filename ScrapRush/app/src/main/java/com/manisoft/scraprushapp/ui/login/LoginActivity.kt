package com.manisoft.scraprushapp.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.manisoft.scraprushapp.databinding.ActivityLoginBinding
import com.manisoft.scraprushapp.ui.numberverification.NumberVerificationActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setClickListeners()
    }

    private fun setClickListeners() {
        binding.btnMobile.setOnClickListener {
            startActivity(Intent(this, NumberVerificationActivity::class.java))
            finish()
        }
    }
}