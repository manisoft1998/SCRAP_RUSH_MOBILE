package com.manisoft.scraprushapp.ui.customer.dashboard

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.manisoft.scraprushapp.databinding.ActivityBackDropBinding

class BackDropActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBackDropBinding
    private lateinit var sheetBehavior: BottomSheetBehavior<LinearLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBackDropBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sheetBehavior = BottomSheetBehavior.from(binding.llMainContent)
        sheetBehavior.isFitToContents = false;
        sheetBehavior.isHideable = false;//prevents the boottom sheet from completely hiding off the screen
        sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED;//initially state to fully expanded

        binding.appBar.root.setOnClickListener {
            toggleFilters()
        }

    }

    private fun toggleFilters() {
        if (sheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED)
        } else {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
        }
    }
}