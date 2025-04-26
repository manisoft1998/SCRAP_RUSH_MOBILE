package com.manisoft.scraprushapp.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

//get address list response
data class AddressListResponse(var `data`: List<AddressData> = listOf(), var message: String? = "", var status: Boolean = false)

@Parcelize
data class AddressData(var address: String? = "",
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
                       var zipcode: Int? = 0):Parcelable

//save address request
data class SaveAddressRequest(var action: String = "", var address: SaveAddress = SaveAddress(), var id: Int = 0)

data class SaveAddress(var address: String = "",
                       var address_type: String = "",
                       var address_type_label: String = "",
                       var city: String = "",
                       var country_id: Int = 0,
                       var email: String = "",
                       var formatted_address: String = "",
                       var latitude: Double = 0.0,
                       var longitude: Double = 0.0,
                       var mobile: String = "",
                       var name: String = "",
                       var place_id: String = "",
                       var state: String = "",
                       var zipcode: Int = 0)

//save address response
data class SaveAddressResponse(var `data`: SavedAddress = SavedAddress(), var message: String = "", var status: Boolean = false)

data class SavedAddress(var address: String = "",
                        var address_type: String = "",
                        var address_type_label: Any = Any(),
                        var city: String = "",
                        var country_id: Int = 0,
                        var created_at: String = "",
                        var distance: Int = 0,
                        var email_address: String = "",
                        var formatted_address: String = "",
                        var id: Int = 0,
                        var latitude: Double = 0.0,
                        var longitude: Double = 0.0,
                        var mobile: String = "",
                        var name: String = "",
                        var place_id: String = "",
                        var state: String = "",
                        var status: String = "",
                        var updated_at: String = "",
                        var user_id: Int = 0,
                        var zipcode: Int = 0)

//delete address request
data class DeleteAddressRequest(
    var address_id: Int = 0
)
//delete address response
data class DeleteAddressResponse(
    var `data`: Any = Any(),
    var message: String = "",
    var status: Boolean = false
)