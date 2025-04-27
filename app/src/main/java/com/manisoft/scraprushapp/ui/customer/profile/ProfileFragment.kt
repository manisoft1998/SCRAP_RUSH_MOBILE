package com.manisoft.scraprushapp.ui.customer.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.manisoft.scraprushapp.R
import com.manisoft.scraprushapp.databinding.FragmentProfileBinding
import com.manisoft.scraprushapp.network.Resource
import com.manisoft.scraprushapp.mvvm.viewmodel.LoginViewModel
import com.manisoft.scraprushapp.ui.customer.login.LoginActivity
import com.manisoft.scraprushapp.utils.Constants
import com.manisoft.scraprushapp.utils.DialogUtils
import com.manisoft.scraprushapp.utils.KeyValues
import com.manisoft.scraprushapp.utils.Utils
import com.manisoft.scraprushapp.utils.negativeButton
import com.manisoft.scraprushapp.utils.positiveButton
import com.manisoft.scraprushapp.utils.shareDeepLink
import com.manisoft.scraprushapp.utils.showAlertDialog
import com.manisoft.scraprushapp.utils.showToast
import com.manisoft.scraprushapp.utils.showUnAuthAlert
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val viewModel: LoginViewModel by viewModel()
    private lateinit var progressDialog: DialogUtils

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        updateUI()
        getInstanceOfClasses()
        setClickListeners()
        subscribe()
    }

    private fun updateUI() {
        binding.tvUsername.text = KeyValues.readString(Constants.FULL_NAME, "") ?: "--"
        binding.tvUserMail.text = KeyValues.readString(Constants.EMAIL, "") ?: "--"
    }

    private fun getInstanceOfClasses() {
        progressDialog = DialogUtils(requireActivity())
    }

    private fun subscribe() {
        viewModel.deleteAccountResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Failure -> {
                    progressDialog.dismiss()
                    if (it.errorCode == 401) showUnAuthAlert() else showToast(it.errorBody.toString())
                }

                is Resource.Loading -> {
                    progressDialog.show()
                }

                is Resource.Success -> {
                    showToast(it.value.message)
                    if (it.value.status) {
                        logout()
                    }
                }
            }
        }
        viewModel.logoutAccountResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Failure -> {
                    progressDialog.dismiss()
                    if (it.errorCode == 401) showUnAuthAlert() else showToast(it.errorBody.toString())
                }

                is Resource.Loading -> {
                    progressDialog.show()
                }

                is Resource.Success -> {
                    showToast(it.value.message)
                    if (it.value.status) {
                        logout()
                    }
                }
            }
        }
    }

    private fun setClickListeners() {
        binding.llLogout.setOnClickListener {
            showAlertDialog {
                setTitle("Logout")
                setMessage("Are you sure want to logout ?")
                positiveButton("Logout") { viewModel.logout(KeyValues.authToken()) }
                negativeButton("No") { it.dismiss() }
            }
        }

        binding.llDelete.setOnClickListener {
            showAlertDialog {
                setTitle("Account Delete")
                setMessage("Are you sure want to delete this account ?")
                positiveButton("Delete") { viewModel.deleteAccount(KeyValues.authToken()) }
                negativeButton("No") { it.dismiss() }
            }
        }
        binding.llAccount.setOnClickListener {
            findNavController().navigate(R.id.action_myAccountFragment_to_accountFragment)
        }
        binding.llWhatsappChat.setOnClickListener {
            Utils.openWhatsAppChat(requireContext())
        }
        binding.llReferAndEarn.setOnClickListener {
            shareDeepLink()
        }
        binding.llPrivacyPolicy.setOnClickListener {
            findNavController().navigate(R.id.action_myAccountFragment_to_privacyPolicyFragment)
        }
        binding.llTermsAndConditions.setOnClickListener {
            findNavController().navigate(R.id.action_myAccountFragment_to_termsAndConditionsFragment)
        }
        binding.llAddAddress.setOnClickListener {
            findNavController().navigate(R.id.action_myAccountFragment_to_addressFragment)
        }
    }

    private fun logout() {
        Firebase.auth.signOut()
        KeyValues.sharedPreferencesClear()
        Intent(requireActivity(), LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(this)
        }
        requireActivity().finish()
    }
}