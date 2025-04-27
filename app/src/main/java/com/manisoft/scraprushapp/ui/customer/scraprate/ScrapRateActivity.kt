package com.manisoft.scraprushapp.ui.customer.scraprate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.manisoft.scraprushapp.adapters.ScrapListAdapter
import com.manisoft.scraprushapp.databinding.ActivityScrapRateBinding
import com.manisoft.scraprushapp.models.ScrapRateItems
import com.manisoft.scraprushapp.mvvm.viewmodel.ScrapViewModel
import com.manisoft.scraprushapp.network.Resource
import com.manisoft.scraprushapp.utils.DialogUtils
import com.manisoft.scraprushapp.utils.KeyValues
import com.manisoft.scraprushapp.utils.showToast
import com.manisoft.scraprushapp.utils.showUnAuthAlert
import org.koin.androidx.viewmodel.ext.android.viewModel

class ScrapRateActivity : AppCompatActivity(), ScrapListAdapter.ScrapClickListener {
    private lateinit var binding: ActivityScrapRateBinding
    private val viewModel: ScrapViewModel by viewModel()
    private lateinit var progressDialog: DialogUtils
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScrapRateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getInstanceOfClasses()
        setClickListener()
        subscribe()
    }

    private fun setClickListener() {
        binding.ivBacKIcon.setOnClickListener {
            onBackPressed()
        }
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
                    if (it.errorCode == 401) showUnAuthAlert() else showToast(it.errorBody.toString())
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
            adapter = ScrapListAdapter(data as ArrayList<ScrapRateItems>, this@ScrapRateActivity)
        }

    }

    override fun onScrapClicked(items: ScrapRateItems, position: Int) {

    }
}