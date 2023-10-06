package com.manisoft.scraprushapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.util.Util
import com.manisoft.scraprushapp.adapters.ScrapRateListAdapter
import com.manisoft.scraprushapp.databinding.ActivityScrapRateBinding
import com.manisoft.scraprushapp.models.ScrapRateItems
import com.manisoft.scraprushapp.mvvm.viewmodel.ScrapViewModel
import com.manisoft.scraprushapp.network.Resource
import com.manisoft.scraprushapp.utils.DialogUtils
import com.manisoft.scraprushapp.utils.KeyValues
import com.manisoft.scraprushapp.utils.Utils
import com.manisoft.scraprushapp.utils.showToast
import org.koin.androidx.viewmodel.ext.android.viewModel

class ScrapRateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScrapRateBinding
    private val viewModel: ScrapViewModel by viewModel()
    private lateinit var progressDialog: DialogUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScrapRateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getInstanceOfClasses()
        subscribe()
    }

    private fun getInstanceOfClasses() {
        progressDialog = DialogUtils(this)
    }

    private fun subscribe() {
        viewModel.getScrapRateItems(KeyValues.authToken())
        viewModel.scrapRateResponse.observe(this) {
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
                    if (it.value.status) {
                        initScrapRecyclerView(it.value.data)
                    } else {
                        showToast(it.value.message ?: "")
                    }
                }
            }
        }
    }

    private fun initScrapRecyclerView(data: List<ScrapRateItems>) {
        binding.rvScraps.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@ScrapRateActivity)
            adapter = ScrapRateListAdapter(data)
        }

    }
}