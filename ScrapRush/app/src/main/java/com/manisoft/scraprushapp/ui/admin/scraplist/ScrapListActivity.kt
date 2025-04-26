package com.manisoft.scraprushapp.ui.admin.scraplist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.manisoft.scraprushapp.adapters.ScrapListAdapter
import com.manisoft.scraprushapp.databinding.ActivityScrapListBinding
import com.manisoft.scraprushapp.models.NewProductRequest
import com.manisoft.scraprushapp.models.ScrapRateItems
import com.manisoft.scraprushapp.mvvm.viewmodel.AdminViewModel
import com.manisoft.scraprushapp.mvvm.viewmodel.ScrapViewModel
import com.manisoft.scraprushapp.network.Resource
import com.manisoft.scraprushapp.ui.admin.newproduct.NewProductActivity
import com.manisoft.scraprushapp.utils.ActivityStatus
import com.manisoft.scraprushapp.utils.DialogUtils
import com.manisoft.scraprushapp.utils.KeyValues
import com.manisoft.scraprushapp.utils.negativeButton
import com.manisoft.scraprushapp.utils.neutralButton
import com.manisoft.scraprushapp.utils.positiveButton
import com.manisoft.scraprushapp.utils.showAlertDialog
import com.manisoft.scraprushapp.utils.showToast
import com.manisoft.scraprushapp.utils.showUnAuthAlert
import org.koin.androidx.viewmodel.ext.android.viewModel

class ScrapListActivity : AppCompatActivity(), ScrapListAdapter.ScrapClickListener {
    private lateinit var binding: ActivityScrapListBinding

    private lateinit var progressDialog: DialogUtils
    private lateinit var scrapAdaptor: ScrapListAdapter
    private lateinit var productUpdateDialog: ProductUpdateDialog

    private val adminViewModel: AdminViewModel by viewModel()
    private val scrapViewModel: ScrapViewModel by viewModel()

    private var deleteClicked = false
    private var selectedProductPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScrapListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getInstanceOfClasses()
        setClickListeners()
        subscribe()
        scrapViewModel.getScrapRateItems(KeyValues.authToken())
    }

    override fun onResume() {
        super.onResume()
        if (ActivityStatus.isProductAdded) {
            ActivityStatus.isProductAdded = false
            scrapViewModel.getScrapRateItems(KeyValues.authToken())
        }
    }

    private fun setClickListeners() {
        binding.fabNewProduct.setOnClickListener {
            Intent(this@ScrapListActivity, NewProductActivity::class.java).apply {
                startActivity(this)
            }
        }
    }

    private fun getInstanceOfClasses() {
        progressDialog = DialogUtils(this)
        productUpdateDialog = ProductUpdateDialog(this, this) { scrapItem, productId ->
            val productRequest = NewProductRequest(action = "update", item_id = productId, data = arrayListOf(scrapItem))
            adminViewModel.saveOrUpdateProduct(KeyValues.authToken(), productRequest)
        }
    }

    private fun subscribe() {
        scrapViewModel.scrapRateResponse.observe(this) {
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

        adminViewModel.productResponse.observe(this) {
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
                        if (deleteClicked) {
                            deleteClicked = false
                            scrapAdaptor.deleteProduct(selectedProductPosition)
                        } else {
                            scrapAdaptor.clearScrapList()
                            scrapViewModel.getScrapRateItems(KeyValues.authToken())
                        }
                    }
                }
            }
        }

    }

    private fun initScrapRecyclerView(data: List<ScrapRateItems>) {
        scrapAdaptor = ScrapListAdapter(data as ArrayList<ScrapRateItems>, this@ScrapListActivity)
        binding.rvScraps.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@ScrapListActivity)
            adapter = scrapAdaptor
        }

    }

    override fun onScrapClicked(items: ScrapRateItems, position: Int) {
        showAlertDialog {
            setTitle("Product options")
            positiveButton("Update") {
                productUpdateDialog.showProductUpdateDialog(items)
            }
            negativeButton("Delete") {
                deleteProduct(position, items)
            }
            neutralButton("Cancel") {
                it.dismiss()
            }
        }
    }

    private fun deleteProduct(position: Int, items: ScrapRateItems) {
        deleteClicked = true
        selectedProductPosition = position
        val productRequest = NewProductRequest(action = "delete", item_id = items.id, data = arrayListOf())
        adminViewModel.saveOrUpdateProduct(KeyValues.authToken(), productRequest)
    }
}