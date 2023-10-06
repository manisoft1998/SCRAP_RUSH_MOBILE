package com.manisoft.scraprushapp.mvvm.repositories

import com.manisoft.scraprushapp.models.LoginRequest
import com.manisoft.scraprushapp.models.UpdateProfileRequest
import com.manisoft.scraprushapp.network.SafeApiCall
import com.manisoft.scraprushapp.network.ApiService
import com.manisoft.scraprushapp.utils.Constants
import com.manisoft.scraprushapp.utils.KeyValues

class ScrapRepository constructor(private val apiService: ApiService) : SafeApiCall {
    suspend fun getScrapRateItems(token:String) = safeApiCall {
        apiService.getScrapRateItems(token)
    }
}