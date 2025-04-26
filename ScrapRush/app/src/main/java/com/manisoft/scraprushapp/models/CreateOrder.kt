package com.manisoft.scraprushapp.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class CreateOrderResponse(var `data`: CreateOrderData? = CreateOrderData(), var message: String? = "", var status: Boolean? = false)

data class CreateOrderData(var created_at: String? = "",
                           var estimation_amount: String? = "",
                           var id: Int? = 0,
                           var is_paid: Int? = 0,
                           var payable_amount: Int? = 0,
                           var pick_up_date: String? = "",
                           var updated_at: String? = "",
                           var user_id: Int? = 0)

//my order response
data class MyOrderResponse(var `data`: ArrayList<MyOrderData?>? = arrayListOf(), var message: String? = "", var status: Boolean? = false)

@Parcelize
data class MyOrderData(var estimation_amount: String? = "",
                       var id: Int? = 0,
                       var images: List<String?>? = listOf(),
                       var is_paid: Int? = 0,
                       var order_id: String? = "",
                       var ordered_at: String? = "",
                       var ordered_items: List<OrderedItem?>? = listOf(),
                       var payable_amount: String? = "",
                       var pick_up_date: String? = "",
                       var status: Int? = 0,
                       var status_history: List<StatusHistory?>? = listOf(),
                       var user_id: Int? = 0,
                       var address: AddressData? = AddressData(),
                       var notes: String?="") : Parcelable

@Parcelize
data class OrderedItem(var amount: String? = "", var id: Int? = 0, var item: String? = "", var unit: String? = "", var weight: String? = "") : Parcelable

@Parcelize
data class StatusHistory(var id: Int? = 0, var is_active: Boolean? = false, var last_updated: String? = "", var name: String? = "", var status_code: Int? = 0) : Parcelable

//update order request
data class UpdateOrderRequest(var order_id: Int? = 0, var status_code: Int? = 0)

//update order response
data class UpdateOrderResponse(var `data`: Any? = Any(), var message: String? = "", var status: Boolean? = false)