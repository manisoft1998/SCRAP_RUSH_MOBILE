package com.manisoft.scraprushapp.mvvm.repositories

import com.manisoft.scraprushapp.models.AdminOrderListRequest
import com.manisoft.scraprushapp.models.AdminUpdateOrderRequest
import com.manisoft.scraprushapp.models.NewProductRequest
import com.manisoft.scraprushapp.network.SafeApiCall
import com.manisoft.scraprushapp.network.ApiService

class AdminRepository(private val apiService: ApiService) : SafeApiCall {

    suspend fun getAdminOrderList(key: String, request: AdminOrderListRequest) = safeApiCall {
        apiService.getAdminOrderList(key, request)
    }

    suspend fun adminUpdateOrder(token: String, request: AdminUpdateOrderRequest) = safeApiCall {
        apiService.adminUpdateOrder(token, request)
    }

    suspend fun saveOrUpdateProduct(token: String, request: NewProductRequest) = safeApiCall {
        apiService.saveOrUpdateProduct(token, request)
    }
}