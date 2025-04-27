package com.manisoft.scraprushapp.ui.customer.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.manisoft.scraprushapp.databinding.ActivityLoginBinding
import com.manisoft.scraprushapp.ui.customer.numberverification.NumberVerificationActivity
import com.manisoft.scraprushapp.ui.customer.profile.privacypolicy.PrivacyPolicyDialog
import com.manisoft.scraprushapp.ui.customer.profile.termsandconditions.TermsAndConditionsDialog


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
        binding.tvPrivacyPolicy.setOnClickListener {
            PrivacyPolicyDialog(this@LoginActivity).showPrivacyPolicyDialog()
        }
        binding.tvTermsAndConditions.setOnClickListener {
            TermsAndConditionsDialog(this@LoginActivity).showTermsAndConditionsDialog()
        }
    }
}