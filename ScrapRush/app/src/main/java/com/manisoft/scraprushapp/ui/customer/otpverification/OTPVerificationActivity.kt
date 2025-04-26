package com.manisoft.scraprushapp.ui.customer.otpverification

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.manisoft.scraprushapp.databinding.ActivityOtpverificationBinding
import com.manisoft.scraprushapp.models.LoginData
import com.manisoft.scraprushapp.models.LoginRequest
import com.manisoft.scraprushapp.network.Resource
import com.manisoft.scraprushapp.mvvm.viewmodel.LoginViewModel
import com.manisoft.scraprushapp.ui.MainActivity
import com.manisoft.scraprushapp.ui.admin.dashboard.AdminDashboardActivity
import com.manisoft.scraprushapp.ui.customer.profile.CompleteProfileActivity
import com.manisoft.scraprushapp.ui.customer.profile.privacypolicy.PrivacyPolicyDialog
import com.manisoft.scraprushapp.ui.customer.profile.termsandconditions.TermsAndConditionsDialog
import com.manisoft.scraprushapp.utils.Constants
import com.manisoft.scraprushapp.utils.DialogUtils
import com.manisoft.scraprushapp.utils.KeyValues
import com.manisoft.scraprushapp.utils.UserRoles
import com.manisoft.scraprushapp.utils.changeStatusBarColor
import com.manisoft.scraprushapp.utils.showToast
import com.poovam.pinedittextfield.PinField
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit


class OTPVerificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOtpverificationBinding
    private var verificationId = ""
    private lateinit var progressDialog: DialogUtils
    private lateinit var firebaseAuth: FirebaseAuth
    private var otpValue = ""
    private var mobileNo = ""
    private val viewModel: LoginViewModel by viewModel()
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpverificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        changeStatusBarColor()

        getInstanceOfClasses()
        subscribe()
        setClickListeners()
        setOtpTimer()
    }

    private fun resendOTP() {
        progressDialog.show()
        val options = PhoneAuthOptions.newBuilder(firebaseAuth).setPhoneNumber("+91$mobileNo") // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this@OTPVerificationActivity) // Activity (for callback binding)
                .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
                .setForceResendingToken(resendToken!!) // ForceResendingToken from callbacks
                .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun subscribe() {
        viewModel.loginResponse.observe(this) {
            when (it) {
                is Resource.Failure -> {
                    progressDialog.dismiss()
                    showToast(it.errorBody?.message ?: "")
                }

                is Resource.Loading -> {
                    progressDialog.show()
                }

                is Resource.Success -> {
                    progressDialog.dismiss()
                    showToast(it.value.message)
                    if (it.value.status) {
                        val isProfileNotCompleted = it.value.data.user.name.isNullOrEmpty() && it.value.data.user.email_address.isNullOrEmpty() && it.value.data.user.dob.isNullOrEmpty()
                        saveUserData(it.value.data, isProfileNotCompleted)

                        when (it.value.data.user.role) {
                            UserRoles.SUPER_ADMIN, UserRoles.ADMIN -> {
                                startActivity(Intent(this@OTPVerificationActivity, AdminDashboardActivity::class.java))
                                finish()
                            }

                            UserRoles.CUSTOMER -> {
                                if (it.value.data.is_new_user || isProfileNotCompleted) {
                                    startActivity(Intent(this@OTPVerificationActivity, CompleteProfileActivity::class.java))
                                    finish()
                                } else {
                                    startActivity(Intent(this@OTPVerificationActivity, MainActivity::class.java))
                                    finish()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setOtpTimer() {
        binding.tvResendOtp.setTextColor(ContextCompat.getColor(this@OTPVerificationActivity, com.manisoft.scraprushapp.R.color.divider))
        binding.tvResendOtp.isEnabled = false

        val d = object : CountDownTimer(60 * 1000, 1 * 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                val minutes = seconds / 60
                val remainingSeconds = seconds % 60

                // Format the remaining time as "mm:ss"
                val formattedTime = String.format("%02d:%02d", minutes, remainingSeconds)
                binding.tvTimer.text = formattedTime
            }

            override fun onFinish() {
                binding.tvResendOtp.setTextColor(ContextCompat.getColor(this@OTPVerificationActivity, com.manisoft.scraprushapp.R.color.green_font))
                binding.tvResendOtp.isEnabled = true
            }
        }
        d.start()
    }


    private fun saveUserData(data: LoginData, isProfileNotCompleted: Boolean) {

        KeyValues.writeBoolean(Constants.LOGIN_STATUS, true)
        KeyValues.writeString(Constants.USER_ROLE, data.user.role ?: "")
        KeyValues.writeBoolean(Constants.PROFILE_STATUS, isProfileNotCompleted)
        KeyValues.writeString(Constants.ACCESS_TOKEN, data.user.access_token ?: "")
        KeyValues.writeLong(Constants.MOBILE_NUMBER, data.user.mobile_number ?: 0L)

        if (!data.is_new_user && !isProfileNotCompleted) {
            KeyValues.writeString(Constants.FULL_NAME, data.user.name ?: "")
            KeyValues.writeString(Constants.EMAIL, data.user.email_address ?: "")
            KeyValues.writeString(Constants.USER_IMAGE, data.user.image ?: "")
        }
    }

    private fun getInstanceOfClasses() {
        verificationId = intent.getStringExtra("verificationId") ?: ""
        mobileNo = intent.getStringExtra("mobileNumber") ?: ""
        resendToken = intent.getParcelableExtra("resendToken")
        progressDialog = DialogUtils(this)

        firebaseAuth = Firebase.auth

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d("TAG", "onVerificationCompleted:$credential")
                progressDialog.dismiss()
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w("TAG", "onVerificationFailed", e)
                progressDialog.dismiss()

                when (e) {
                    is FirebaseAuthInvalidCredentialsException -> {
                        // Invalid request
                        showToast(e.message ?: "OTP Verification Failed")
                    }

                    is FirebaseTooManyRequestsException -> {
                        showToast(e.message ?: "OTP Verification Failed")
                        // The SMS quota for the project has been exceeded
                    }

                    is FirebaseAuthMissingActivityForRecaptchaException -> {
                        showToast(e.message ?: "OTP Verification Failed")
                    }
                }

                // Show a message and update the UI
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken,
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d("TAG", "onCodeSent:$verificationId")
                progressDialog.dismiss()
                setOtpTimer()
                showToast("Verification code has been sent to your phone number")
                // Save verification ID and resending token so we can use them later
            }
        }

    }

    private fun setClickListeners() {
        binding.btnVerifyOtp.setOnClickListener {
            verifyPhoneNumberWithCode(verificationId, otpValue)
        }
        val listener = object : PinField.OnTextCompleteListener {
            override fun onTextComplete(enteredText: String): Boolean {
                if (enteredText.isNotEmpty()) otpValue = enteredText
                binding.btnVerifyOtp.isEnabled = enteredText.length == 6
                return true
            }
        }
        binding.otpEt.onTextCompleteListener = listener

        binding.tvResendOtp.setOnClickListener {
            resendOTP()
        }
        binding.tvPrivacyPolicy.setOnClickListener {
            PrivacyPolicyDialog(this@OTPVerificationActivity).showPrivacyPolicyDialog()
        }
        binding.tvTermsAndConditions.setOnClickListener {
            TermsAndConditionsDialog(this@OTPVerificationActivity).showTermsAndConditionsDialog()
        }
    }

    private fun verifyPhoneNumberWithCode(verificationId: String?, code: String) {
        signInWithPhoneAuthCredential(PhoneAuthProvider.getCredential(verificationId ?: "", code))
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        progressDialog.show()
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            progressDialog.dismiss()
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d("OTPVerificationActivity", "signInWithCredential:success")
                showToast("OTP Verified Successfully")
                val user = task.result?.user

                viewModel.login(LoginRequest(mobile_number = mobileNo.toLong()))
            } else {
                // Sign in failed, display a message and update the UI
                Log.w("OTPVerificationActivity", "signInWithCredential:failure", task.exception)
                if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    // The verification code entered was invalid
                    showToast(task.exception?.message ?: "")
                }
                // Update UI
            }
        }
    }


}