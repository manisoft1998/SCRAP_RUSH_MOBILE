package com.manisoft.scraprushapp.mvvm.repositories

import com.manisoft.scraprushapp.models.DeleteAddressRequest
import com.manisoft.scraprushapp.models.LoginRequest
import com.manisoft.scraprushapp.models.SaveAddressRequest
import com.manisoft.scraprushapp.models.UpdateProfileRequest
import com.manisoft.scraprushapp.network.SafeApiCall
import com.manisoft.scraprushapp.network.ApiService
import com.manisoft.scraprushapp.utils.Constants
import com.manisoft.scraprushapp.utils.KeyValues

class AddressRepository constructor(private val apiService: ApiService) : SafeApiCall {
    suspend fun addressList(token: String) = safeApiCall {
        apiService.addressList(token)
    }

    suspend fun saveAddress(token: String, request: SaveAddressRequest) = safeApiCall {
        apiService.saveAddress(token, request)
    }

    suspend fun deleteAddress(token: String, request: DeleteAddressRequest) = safeApiCall {
        apiService.addressDelete(token, request)
    }
}