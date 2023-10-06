package com.manisoft.scraprushapp.ui.requestpickup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.manisoft.scraprushapp.adapters.SelectScrapListAdapter
import com.manisoft.scraprushapp.databinding.ActivitySelectScrapItemBinding
import com.manisoft.scraprushapp.utils.DummyList

class SelectScrapItemActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectScrapItemBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectScrapItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadScrapItems()
        setClickListeners()
    }

    private fun setClickListeners() {
        binding.btnProceed.setOnClickListener {
            startActivity(Intent(this@SelectScrapItemActivity, RequestPickupActivity::class.java))
        }
    }

    private fun loadScrapItems() {
        binding.rvScraps.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(this@SelectScrapItemActivity, 3)
            adapter = SelectScrapListAdapter(arrayListOf())
        }
    }
}