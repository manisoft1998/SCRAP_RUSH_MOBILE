package com.manisoft.scraprushapp.ui.numberverification

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.ktx.appCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.manisoft.scraprushapp.databinding.ActivityNumberVerificationBinding
import com.manisoft.scraprushapp.ui.MainActivity
import com.manisoft.scraprushapp.ui.otpverification.OTPVerificationActivity
import com.manisoft.scraprushapp.utils.DialogUtils
import com.manisoft.scraprushapp.utils.changeStatusBarColor
import com.manisoft.scraprushapp.utils.onTextChanged
import com.manisoft.scraprushapp.utils.showToast
import java.util.concurrent.TimeUnit

class NumberVerificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNumberVerificationBinding
    private lateinit var progressDialog: DialogUtils
    private lateinit var firebaseAuth: FirebaseAuth

    private var storedVerificationId: String? = ""
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNumberVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        changeStatusBarColor()

        getInstanceOfClasses()
//        initDebug()
        init()
        setClickListeners()
    }

    private fun getInstanceOfClasses() {
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
                        showToast(e.message ?: "")
                    }

                    is FirebaseTooManyRequestsException -> {
                        showToast(e.message ?: "")
                        // The SMS quota for the project has been exceeded
                    }

                    is FirebaseAuthMissingActivityForRecaptchaException -> {
                        // reCAPTCHA verification attempted with null Activity
                        showToast(e.message ?: "")
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
                showToast("Verification code has been sent to your phone number")
                // Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId
                resendToken = token
                startActivity(Intent(this@NumberVerificationActivity, OTPVerificationActivity::class.java).putExtra("verificationId", verificationId)
                        .putExtra("mobileNumber", binding.etMobileNumber.text.toString().trim()).putExtra("resendToken", token))
                finish()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setClickListeners() {
        binding.btnSendOtp.setOnClickListener {
            when {
                binding.etMobileNumber.text.toString().isEmpty() -> showToast("Please enter the phone number")
                else -> startPhoneNumberVerification("+91" + binding.etMobileNumber.text.toString().trim())
            }
        }

        binding.etMobileNumber.onTextChanged {
            // Calculate and display the character count
            val currentLength = it.length
            val maxLength = 10 // Set your desired maximum character count here
            binding.tvPhoneNumberCount.text = "$currentLength/$maxLength"
            binding.btnSendOtp.isEnabled = it.length == 10
        }
    }


    private fun startPhoneNumberVerification(phoneNumber: String) {
        progressDialog.show()
        val options = PhoneAuthOptions.newBuilder(firebaseAuth).setPhoneNumber(phoneNumber) // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this) // Activity (for callback binding)
                .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
                .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }


    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d("TAG", "signInWithCredential:success")
                startActivity(Intent(this@NumberVerificationActivity, MainActivity::class.java))
                finish()
            } else {
                // Sign in failed, display a message and update the UI
                Log.w("TAG", "signInWithCredential:failure", task.exception)
                if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    // The verification code entered was invalid
                    showToast("Verification Failed")
                }
            }
        }
    }

    private fun init() {
        // [START appcheck_initialize]
        Firebase.initialize(context = this)
        Firebase.appCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance(),
        )
        // [END appcheck_initialize]
    }

    private fun initDebug() {
        // [START appcheck_initialize_debug]
        Firebase.initialize(context = this)
        Firebase.appCheck.installAppCheckProviderFactory(
            DebugAppCheckProviderFactory.getInstance(),
        )
        // [END appcheck_initialize_debug]
    }
}