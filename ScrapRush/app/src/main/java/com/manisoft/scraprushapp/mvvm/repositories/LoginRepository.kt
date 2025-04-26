package com.manisoft.scraprushapp.mvvm.repositories

import com.manisoft.scraprushapp.models.LoginRequest
import com.manisoft.scraprushapp.models.UpdateProfileRequest
import com.manisoft.scraprushapp.network.SafeApiCall
import com.manisoft.scraprushapp.network.ApiService
import com.manisoft.scraprushapp.utils.Constants
import com.manisoft.scraprushapp.utils.KeyValues

class LoginRepository(private val apiService: ApiService) : SafeApiCall {
    suspend fun login(requestBody: LoginRequest) = safeApiCall {
        apiService.login(requestBody)
    }

    suspend fun updateProfile(requestBody: UpdateProfileRequest, accessToken: String) = safeApiCall {
        apiService.updateProfile(requestBody, accessToken)
    }

    suspend fun deleteAccount(accessToken: String) = safeApiCall {
        apiService.deleteAccount(accessToken)
    }

    suspend fun logout(accessToken: String) = safeApiCall {
        apiService.logout(accessToken)
    }
}