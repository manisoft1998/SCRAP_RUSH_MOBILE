package com.manisoft.scraprushapp.mvvm.repositories

import com.manisoft.scraprushapp.models.LoginRequest
import com.manisoft.scraprushapp.models.UpdateFCMTokenRequest
import com.manisoft.scraprushapp.models.UpdateOrderRequest
import com.manisoft.scraprushapp.models.UpdateProfileRequest
import com.manisoft.scraprushapp.network.SafeApiCall
import com.manisoft.scraprushapp.network.ApiService
import com.manisoft.scraprushapp.utils.Constants
import com.manisoft.scraprushapp.utils.KeyValues
import okhttp3.MultipartBody
import okhttp3.RequestBody

class ScrapRepository(private val apiService: ApiService) : SafeApiCall {
    suspend fun getScrapRateItems(token: String) = safeApiCall {
        apiService.getScrapRateItems(token)
    }

    suspend fun createOrder(
        token: String,
        items: RequestBody,
        estimationAmount: RequestBody,
        addressId: RequestBody,
        pickupDate: RequestBody,
        pickupTime: RequestBody,
        notes: RequestBody?,
        image: MultipartBody.Part?,
    ) = safeApiCall {
        apiService.createOrder(token, items, estimationAmount, addressId, pickupDate, pickupTime, notes,image)
    }

    suspend fun getMyOrder(token: String) = safeApiCall {
        apiService.getMyOrders(token)
    }

    suspend fun updateOrder(token: String, request: UpdateOrderRequest) = safeApiCall {
        apiService.updateOrder(token, request)
    }

    suspend fun updateFcmToken(token: String, request: UpdateFCMTokenRequest) = safeApiCall {
        apiService.updateFcmToken(token, request)
    }
}