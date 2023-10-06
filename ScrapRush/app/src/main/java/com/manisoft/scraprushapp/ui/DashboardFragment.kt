package com.manisoft.scraprushapp.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.manisoft.scraprushapp.databinding.FragmentDashboardBinding
import com.manisoft.scraprushapp.ui.requestpickup.SelectScrapItemActivity


class DashboardFragment : Fragment() {
    private lateinit var binding: FragmentDashboardBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setClickListeners()
    }

    private fun setClickListeners() {
        binding.dashboardMenu.cvScrapRate.setOnClickListener {
            startActivity(Intent(requireActivity(), ScrapRateActivity::class.java))
        }
        binding.dashboardMenu.cvRequestPickup.setOnClickListener {
            startActivity(Intent(requireActivity(), SelectScrapItemActivity::class.java))
        }
    }

}