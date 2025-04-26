package com.manisoft.scraprushapp.network

import com.manisoft.scraprushapp.models.AddressListResponse
import com.manisoft.scraprushapp.models.AdminOrderListRequest
import com.manisoft.scraprushapp.models.AdminOrdersResponse
import com.manisoft.scraprushapp.models.AdminUpdateOrderRequest
import com.manisoft.scraprushapp.models.CreateOrderResponse
import com.manisoft.scraprushapp.models.DeleteAddressRequest
import com.manisoft.scraprushapp.models.DeleteAddressResponse
import com.manisoft.scraprushapp.models.LoginRequest
import com.manisoft.scraprushapp.models.LoginResponse
import com.manisoft.scraprushapp.models.MyOrderResponse
import com.manisoft.scraprushapp.models.NewProductRequest
import com.manisoft.scraprushapp.models.SaveAddressRequest
import com.manisoft.scraprushapp.models.SaveAddressResponse
import com.manisoft.scraprushapp.models.ScrapRateResponse
import com.manisoft.scraprushapp.models.UpdateFCMTokenRequest
import com.manisoft.scraprushapp.models.UpdateOrderRequest
import com.manisoft.scraprushapp.models.UpdateOrderResponse
import com.manisoft.scraprushapp.models.UpdateProfileRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
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
    @GET("api/account/logout")
    suspend fun logout(@Header("X-Access-Token") token: String): LoginResponse

    @Headers("Accept: application/json")
    @GET("api/items")
    suspend fun getScrapRateItems(@Header("X-Access-Token") token: String): ScrapRateResponse

    @Headers("Accept: application/json")
    @POST("api/account/address-list")
    suspend fun addressList(@Header("X-Access-Token") token: String): AddressListResponse

    @Headers("Accept: application/json")
    @POST("api/account/address-save")
    suspend fun saveAddress(@Header("X-Access-Token") token: String, @Body request: SaveAddressRequest): SaveAddressResponse

    @Headers("Accept: application/json")
    @POST("api/account/address-delete")
    suspend fun addressDelete(@Header("X-Access-Token") token: String, @Body id: DeleteAddressRequest): DeleteAddressResponse


    @Headers("Accept: application/json")
    @Multipart
    @POST("api/orders/create")
    suspend fun createOrder(
        @Header("X-Access-Token") token: String,
        @Part("items") items: RequestBody,
        @Part("estimation_amount") estimationAmount: RequestBody,
        @Part("address_id") addressId: RequestBody,
        @Part("pick_up_date") pickupDate: RequestBody,
        @Part("pick_up_time") pickupTime: RequestBody,
        @Part("notes") notes: RequestBody?,
        @Part image: MultipartBody.Part? = null,
    ): CreateOrderResponse

    @Headers("Accept: application/json")
    @GET("api/orders/list")
    suspend fun getMyOrders(@Header("X-Access-Token") token: String): MyOrderResponse

    @Headers("Accept: application/json")
    @POST("api/orders/update")
    suspend fun updateOrder(@Header("X-Access-Token") token: String, @Body request: UpdateOrderRequest): UpdateOrderResponse

    @Headers("Accept: application/json")
    @POST("api/admin/order/lists")
    suspend fun getAdminOrderList(@Header("X-Access-Token") token: String, @Body request: AdminOrderListRequest): AdminOrdersResponse

    @Headers("Accept: application/json")
    @POST("api/admin/order/update-status")
    suspend fun adminUpdateOrder(@Header("X-Access-Token") token: String, @Body request: AdminUpdateOrderRequest): UpdateOrderResponse

    @Headers("Accept: application/json")
    @POST("api/admin/item-update")
    suspend fun saveOrUpdateProduct(@Header("X-Access-Token") token: String, @Body request: NewProductRequest): UpdateOrderResponse


    @Headers("Accept: application/json")
    @POST("api/user/update-fcm")
    suspend fun updateFcmToken(@Header("X-Access-Token") token: String, @Body request: UpdateFCMTokenRequest): UpdateOrderResponse


}