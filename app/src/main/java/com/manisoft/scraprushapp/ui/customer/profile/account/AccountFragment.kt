package com.manisoft.scraprushapp.ui.customer.profile.account

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.manisoft.scraprushapp.databinding.FragmentAccountBinding
import com.manisoft.scraprushapp.models.UpdateProfileRequest
import com.manisoft.scraprushapp.mvvm.viewmodel.LoginViewModel
import com.manisoft.scraprushapp.network.Resource
import com.manisoft.scraprushapp.utils.Constants
import com.manisoft.scraprushapp.utils.DialogUtils
import com.manisoft.scraprushapp.utils.KeyValues
import com.manisoft.scraprushapp.utils.Validator
import com.manisoft.scraprushapp.utils.showToast
import com.manisoft.scraprushapp.utils.showUnAuthAlert
import org.koin.androidx.viewmodel.ext.android.viewModel

class AccountFragment : Fragment() {
    private lateinit var binding: FragmentAccountBinding
    private val viewModel: LoginViewModel by viewModel()
    private lateinit var progressDialog: DialogUtils

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getInstanceOfClasses()
        updateUI()
        setClickListeners()
        subscribe()
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI() {
        binding.etName.setText(KeyValues.readString(Constants.FULL_NAME, "") ?: "--")
        binding.etEmailAddress.setText(KeyValues.readString(Constants.EMAIL, "") ?: "--")
        binding.etMobileNumber.setText("${KeyValues.readLong(Constants.MOBILE_NUMBER, 0L) ?: 0L}")
    }

    private fun getInstanceOfClasses() {
        progressDialog = DialogUtils(requireActivity())
    }

    private fun setClickListeners() {
        binding.ivBacKIcon.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.btnUpdate.setOnClickListener {
            validateInputs()
        }
    }

    private fun subscribe() {
        viewModel.updateProfileResponse.observe(viewLifecycleOwner) {
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

    private fun validateInputs() {
        when {
            binding.etName.text.toString().isEmpty() -> showToast("Please enter your name")
            binding.etEmailAddress.text.toString().isEmpty() -> showToast("Please enter email address")
            !Validator.isEmailAddress(binding.etEmailAddress.text.toString()) -> showToast("Please enter valid email address")
            else -> saveProfile()
        }
    }

    private fun saveProfile() {
        viewModel.updateProfile(UpdateProfileRequest(name = binding.etName.text.toString(), email_address = binding.etEmailAddress.text.toString()), KeyValues.authToken())
    }

    private fun saveUserData() {
        KeyValues.writeString(Constants.FULL_NAME, binding.etName.text.toString())
        KeyValues.writeString(Constants.EMAIL, binding.etEmailAddress.text.toString())
    }
}