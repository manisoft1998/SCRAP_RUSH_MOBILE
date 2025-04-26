package com.manisoft.scraprushapp.mvvm.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manisoft.scraprushapp.models.CreateOrderResponse
import com.manisoft.scraprushapp.models.MyOrderResponse
import com.manisoft.scraprushapp.models.ScrapRateResponse
import com.manisoft.scraprushapp.models.UpdateFCMTokenRequest
import com.manisoft.scraprushapp.models.UpdateOrderRequest
import com.manisoft.scraprushapp.models.UpdateOrderResponse
import com.manisoft.scraprushapp.network.Resource
import com.manisoft.scraprushapp.mvvm.repositories.ScrapRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class ScrapViewModel(private val repository: ScrapRepository) : ViewModel() {
    //scrap rate response
    private val _scrapRateResponse = MutableLiveData<Resource<ScrapRateResponse>>()
    val scrapRateResponse: LiveData<Resource<ScrapRateResponse>> get() = _scrapRateResponse

    fun getScrapRateItems(token: String) = viewModelScope.launch {
        _scrapRateResponse.value = Resource.Loading
        _scrapRateResponse.value = repository.getScrapRateItems(token)
    }

    //create order response
    private val _createOrderResponse = MutableLiveData<Resource<CreateOrderResponse>>()
    val createOrderResponse: LiveData<Resource<CreateOrderResponse>> get() = _createOrderResponse
    fun createOrder(
        token: String,
        items: RequestBody,
        estimationAmount: RequestBody,
        addressId: RequestBody,
        pickupDate: RequestBody,
        pickupTime: RequestBody,
        notes: RequestBody?,
        image: MultipartBody.Part?,
    ) = viewModelScope.launch {
        _createOrderResponse.value = Resource.Loading
        _createOrderResponse.value = repository.createOrder(token, items, estimationAmount, addressId, pickupDate, pickupTime, notes, image)
    }

    //my order response
    private val _myOrderResponse = MutableLiveData<Resource<MyOrderResponse>>()
    val myOrderResponse: LiveData<Resource<MyOrderResponse>> get() = _myOrderResponse
    fun getMyOrder(token: String) = viewModelScope.launch {
        _myOrderResponse.value = Resource.Loading
        _myOrderResponse.value = repository.getMyOrder(token)
    }

    //update order response
    private val _updateOrderResponse = MutableLiveData<Resource<UpdateOrderResponse>>()
    val updateOrderResponse: LiveData<Resource<UpdateOrderResponse>> get() = _updateOrderResponse
    fun updateOrder(token: String, orderRequest: UpdateOrderRequest) = viewModelScope.launch {
        _updateOrderResponse.value = Resource.Loading
        _updateOrderResponse.value = repository.updateOrder(token, orderRequest)
    }

    //update fcm token response
    private val _updateFcmTokenResponse = MutableLiveData<Resource<UpdateOrderResponse>>()
    val updateFcmTokenResponse: LiveData<Resource<UpdateOrderResponse>> get() = _updateFcmTokenResponse
    fun updateFcmTokenResponse(token: String, orderRequest: UpdateFCMTokenRequest) = viewModelScope.launch {
        _updateFcmTokenResponse.value = Resource.Loading
        _updateFcmTokenResponse.value = repository.updateFcmToken(token, orderRequest)
    }
}