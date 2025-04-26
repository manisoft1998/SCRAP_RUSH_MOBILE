package com.manisoft.scraprushapp.mvvm.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manisoft.scraprushapp.models.AddressListResponse
import com.manisoft.scraprushapp.models.DeleteAddressRequest
import com.manisoft.scraprushapp.models.DeleteAddressResponse
import com.manisoft.scraprushapp.models.LoginRequest
import com.manisoft.scraprushapp.models.LoginResponse
import com.manisoft.scraprushapp.models.SaveAddressRequest
import com.manisoft.scraprushapp.models.SaveAddressResponse
import com.manisoft.scraprushapp.models.ScrapRateResponse
import com.manisoft.scraprushapp.models.UpdateProfileRequest
import com.manisoft.scraprushapp.mvvm.repositories.AddressRepository
import com.manisoft.scraprushapp.network.Resource
import com.manisoft.scraprushapp.mvvm.repositories.LoginRepository
import com.manisoft.scraprushapp.mvvm.repositories.ScrapRepository
import kotlinx.coroutines.launch

class AddressViewModel(private val repository: AddressRepository) : ViewModel() {
    //address list
    private val _addressListResponse = MutableLiveData<Resource<AddressListResponse>>()
    val addressListResponse: LiveData<Resource<AddressListResponse>> get() = _addressListResponse

    fun addressList(token: String) = viewModelScope.launch {
        _addressListResponse.value = Resource.Loading
        _addressListResponse.value = repository.addressList(token)
    }

    //save address
    private val _saveAddressResponse = MutableLiveData<Resource<SaveAddressResponse>>()
    val saveAddressResponse: LiveData<Resource<SaveAddressResponse>> get() = _saveAddressResponse

    fun saveAddress(token: String, request: SaveAddressRequest) = viewModelScope.launch {
        _saveAddressResponse.value = Resource.Loading
        _saveAddressResponse.value = repository.saveAddress(token, request)
    }

    //delete address
    private val _deleteAddressResponse = MutableLiveData<Resource<DeleteAddressResponse>>()
    val deleteAddressResponse: LiveData<Resource<DeleteAddressResponse>> get() = _deleteAddressResponse
    fun deleteAddress(token: String, request: DeleteAddressRequest) = viewModelScope.launch {
        _deleteAddressResponse.value = Resource.Loading
        _deleteAddressResponse.value = repository.deleteAddress(token, request)
    }
}