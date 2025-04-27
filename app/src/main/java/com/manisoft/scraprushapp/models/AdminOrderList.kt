package com.manisoft.scraprushapp.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

//admin order list request
data class AdminOrderListRequest(var page: Int? = 0, var page_limit: Int? = 0, var status: Int? = 0)

//admin order list response
data class AdminOrdersResponse(var `data`: AdminOrdersData? = AdminOrdersData(), var message: String? = "", var status: Boolean? = false)

@Parcelize
data class AdminOrdersData(var is_last_page: Boolean? = false, var order_count: OrderCount? = OrderCount(), var order_lists: ArrayList<OrderLists?>? = arrayListOf()) : Parcelable

@Parcelize
data class OrderCount(var status_wise: List<StatusWise?>? = listOf(), var total_orders: Int? = 0) : Parcelable

@Parcelize
data class OrderLists(var address: Address? = Address(),
                      var address_id: Int? = 0,
                      var estimation_amount: String? = "",
                      var id: Int? = 0,
                      var images: List<String?>? = listOf(),
                      var is_paid: Int? = 0,
                      var notes: String? = "",
                      var order_id: String? = "",
                      var ordered_at: String? = "",
                      var ordered_items: List<OrderedItem?>? = listOf(),
                      var payable_amount: String? = "",
                      var pick_up_date: String? = "",
                      var status: Int? = 0,
                      var status_history: List<StatusHistory?>? = listOf(),
                      var user_id: Int? = 0) : Parcelable

@Parcelize
data class StatusWise(var id: Int? = 0, var name: String? = "", var status_code: Int? = 0, var total_orders: Int? = 0) : Parcelable

@Parcelize
data class Address(var address: String? = "",
                   var address_type: String? = "",
                   var address_type_label: String? = "",
                   var city: String? = "",
                   var country_id: Int? = 0,
                   var created_at: String? = "",
                   var distance: String? = "",
                   var email_address: String? = "",
                   var formatted_address: String? = "",
                   var id: Int? = 0,
                   var latitude: String? = "",
                   var longitude: String? = "",
                   var mobile: String? = "",
                   var name: String? = "",
                   var place_id: String? = "",
                   var state: String? = "",
                   var status: String? = "",
                   var updated_at: String? = "",
                   var user_id: Int? = 0,
                   var warehouse_updated_at: String? = "",
                   var zipcode: Int? = 0) : Parcelable

//update admin order
data class AdminUpdateOrderRequest(var notess: String? = "", var order_id: Int? = 0, var order_status: Int? = 0)

