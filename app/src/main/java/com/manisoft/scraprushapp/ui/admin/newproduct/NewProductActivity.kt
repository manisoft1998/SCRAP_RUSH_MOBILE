package com.manisoft.scraprushapp.ui.admin.newproduct

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.manisoft.scraprushapp.adapters.adminadapters.NewScrapAdapter
import com.manisoft.scraprushapp.databinding.ActivityNewProductBinding
import com.manisoft.scraprushapp.models.NewProductRequest
import com.manisoft.scraprushapp.mvvm.viewmodel.AdminViewModel
import com.manisoft.scraprushapp.network.Resource
import com.manisoft.scraprushapp.utils.ActivityStatus
import com.manisoft.scraprushapp.utils.DialogUtils
import com.manisoft.scraprushapp.utils.KeyValues
import com.manisoft.scraprushapp.utils.showToast
import org.koin.androidx.viewmodel.ext.android.viewModel


class NewProductActivity : AppCompatActivity(), NewScrapAdapter.NewScrapClickListeners {
    private lateinit var binding: ActivityNewProductBinding

    private lateinit var newProductDialog: NewProductDialog
    private lateinit var newScrapAdapter: NewScrapAdapter
    private lateinit var progressDialog: DialogUtils
    private val viewModel: AdminViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getInstanceOfClasses()
        initProductRecyclerView()
        setClickListeners()
        subscribe()
    }

    private fun initProductRecyclerView() {
        newScrapAdapter = NewScrapAdapter(this)
        binding.rvScraps.apply {
            layoutManager = LinearLayoutManager(this@NewProductActivity)
            adapter = newScrapAdapter
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateItemCount() {
        val itemCount = newScrapAdapter.newProductList.size
        binding.tvItemCount.text = "$itemCount ${if (itemCount == 1) "item" else "items"} added"
    }

    private fun getInstanceOfClasses() {
        progressDialog = DialogUtils(this@NewProductActivity)
        newProductDialog = NewProductDialog(this, this) {
            newScrapAdapter.addNewProduct(it)
            updateItemCount()
        }
    }

    private fun subscribe() {
        viewModel.productResponse.observe(this) {
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
                    showToast(it.value.message ?: "")
                    if (it.value.status == true) {
                        ActivityStatus.isProductAdded = true
                        onBackPressed()
                    }
                }
            }
        }
    }

    private fun setClickListeners() {
        binding.fabNewProduct.setOnClickListener {
            newProductDialog.showNewProductDialog()
        }

        binding.btnSave.setOnClickListener {
            if (newScrapAdapter.newProductList.isEmpty()) {
                showToast("No items to save")
            } else {
                saveNewProducts()
            }
        }
    }

    private fun saveNewProducts() {
        val productRequest = NewProductRequest(action = "create", data = newScrapAdapter.newProductList)
        viewModel.saveOrUpdateProduct(KeyValues.authToken(), productRequest)
    }

    override fun onDeleteClicked(position: Int) {
        newScrapAdapter.deleteProduct(position)
        updateItemCount()
    }
}

//https://mathursanb.medium.com/how-to-upload-images-to-aws-s3-from-the-android-app-a35c603ac9c9