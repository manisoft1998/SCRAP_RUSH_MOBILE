package com.manisoft.scraprushapp.mvvm.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manisoft.scraprushapp.models.AdminOrderListRequest
import com.manisoft.scraprushapp.models.AdminOrdersResponse
import com.manisoft.scraprushapp.models.AdminUpdateOrderRequest
import com.manisoft.scraprushapp.models.NewProductRequest
import com.manisoft.scraprushapp.models.UpdateOrderResponse
import com.manisoft.scraprushapp.mvvm.repositories.AdminRepository
import com.manisoft.scraprushapp.network.Resource
import kotlinx.coroutines.launch

class AdminViewModel(private val repository: AdminRepository) : ViewModel() {

    //admin order response
    private val _adminOrdersResponse = MutableLiveData<Resource<AdminOrdersResponse>>()
    val adminOrdersResponse: LiveData<Resource<AdminOrdersResponse>> get() = _adminOrdersResponse

    fun getAdminOrderList(key: String, request: AdminOrderListRequest) = viewModelScope.launch {
        _adminOrdersResponse.value = Resource.Loading
        _adminOrdersResponse.value = repository.getAdminOrderList(key, request)
    }

    //admin order pagination response
    private val _adminOrdersPaginationResponse = MutableLiveData<Resource<AdminOrdersResponse>>()
    val adminOrdersPaginationResponse: LiveData<Resource<AdminOrdersResponse>> get() = _adminOrdersPaginationResponse

    fun getAdminOrderPaginationList(key: String, request: AdminOrderListRequest) = viewModelScope.launch {
        _adminOrdersPaginationResponse.value = Resource.Loading
        _adminOrdersPaginationResponse.value = repository.getAdminOrderList(key, request)
    }

    //admin update order response
    private val _updateOrderResponse = MutableLiveData<Resource<UpdateOrderResponse>>()
    val updateOrderResponse: LiveData<Resource<UpdateOrderResponse>> get() = _updateOrderResponse
    fun updateOrder(token: String, request: AdminUpdateOrderRequest) = viewModelScope.launch {
        _updateOrderResponse.value = Resource.Loading
        _updateOrderResponse.value = repository.adminUpdateOrder(token, request)
    }

    //save or update new product
    private val _productResponse = MutableLiveData<Resource<UpdateOrderResponse>>()
    val productResponse: LiveData<Resource<UpdateOrderResponse>> get() = _productResponse
    fun saveOrUpdateProduct(token: String, request: NewProductRequest) = viewModelScope.launch {
        _productResponse.value = Resource.Loading
        _productResponse.value = repository.saveOrUpdateProduct(token, request)
    }
}