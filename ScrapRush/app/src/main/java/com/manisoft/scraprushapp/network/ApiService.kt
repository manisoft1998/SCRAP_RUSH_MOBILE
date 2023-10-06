package com.manisoft.scraprushapp.network

import com.manisoft.scraprushapp.models.LoginRequest
import com.manisoft.scraprushapp.models.LoginResponse
import com.manisoft.scraprushapp.models.ScrapRateResponse
import com.manisoft.scraprushapp.models.UpdateProfileRequest
import retrofit2.http.*

interface ApiService {
    // login and register apis
    @POST("api/login")
    suspend fun login(@Body requestBody: LoginRequest): LoginResponse

    @Headers("Accept: application/json")
    @POST("api/account/update-profile")
    suspend fun updateProfile(@Body requestBody: UpdateProfileRequest, @Header("X-Access-Token") token: String): LoginResponse

    @Headers("Accept: application/json")
    @POST("api/account/delete")
    suspend fun deleteAccount(@Header("X-Access-Token") token: String): LoginResponse

    @Headers("Accept: application/json")
    @POST("api/account/logout")
    suspend fun logout(@Header("X-Access-Token") token: String): LoginResponse

    @Headers("Accept: application/json")
    @GET("api/items")
    suspend fun getScrapRateItems(@Header("X-Access-Token") token: String): ScrapRateResponse
}