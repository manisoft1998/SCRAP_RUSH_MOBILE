package com.manisoft.scraprushapp.ui.customer.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.manisoft.scraprushapp.databinding.ActivityCompleteProfileBinding
import com.manisoft.scraprushapp.models.UpdateProfileRequest
import com.manisoft.scraprushapp.network.Resource
import com.manisoft.scraprushapp.mvvm.viewmodel.LoginViewModel
import com.manisoft.scraprushapp.ui.MainActivity
import com.manisoft.scraprushapp.utils.Constants
import com.manisoft.scraprushapp.utils.DialogUtils
import com.manisoft.scraprushapp.utils.KeyValues
import com.manisoft.scraprushapp.utils.Validator
import com.manisoft.scraprushapp.utils.changeStatusBarColor
import com.manisoft.scraprushapp.utils.showToast
import com.manisoft.scraprushapp.utils.showUnAuthAlert
import org.koin.androidx.viewmodel.ext.android.viewModel

class CompleteProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCompleteProfileBinding
    private lateinit var progressDialog: DialogUtils
    private val viewModel: LoginViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompleteProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        changeStatusBarColor()

        getInstanceOfClasses()
        subscribe()
        setClickListeners()
    }

    private fun subscribe() {
        viewModel.updateProfileResponse.observe(this) {
            when (it) {
                is Resource.Failure -> {
                    progressDialog.dismiss()
                    if (it.errorCode == 401) showUnAuthAlert() else showToast(it.errorBody.toString())
                }

                is Resource.Loading -> {
                    progressDialog.show()
                }

                is Resource.Success -> {
                    progressDialog.dismiss()
                    showToast(it.value.message)
                    if (it.value.status) {
                        saveUserData()
                    }
                }
            }
        }
    }

    private fun saveUserData() {
        KeyValues.writeString(Constants.FULL_NAME, binding.etFullName.text.toString())
        KeyValues.writeString(Constants.EMAIL, binding.etEmail.text.toString())
        KeyValues.writeBoolean(Constants.PROFILE_STATUS, false)
        startActivity(Intent(this@CompleteProfileActivity, MainActivity::class.java))
        finish()
    }

    private fun getInstanceOfClasses() {
        progressDialog = DialogUtils(this)
    }

    private fun setClickListeners() {
        binding.btnSubmit.setOnClickListener {
            validateInputs()
        }
        /*        binding.etDob.setOnClickListener {
            Utils.showDatePicker(supportFragmentManager) {
                binding.etDob.setText(it)
            }
        }*/
    }

    private fun validateInputs() {
        when {
            binding.etFullName.text.toString().isEmpty() -> showToast("Please enter your name")
            binding.etEmail.text.toString().isEmpty() -> showToast("Please enter email address")
            !Validator.isEmailAddress(binding.etEmail.text.toString()) -> showToast("Please enter valid email address")
            else -> saveProfile()
        }
    }

    private fun saveProfile() {
        viewModel.updateProfile(UpdateProfileRequest(name = binding.etFullName.text.toString(), email_address = binding.etEmail.text.toString()), KeyValues.authToken())
    }

}
