package com.manisoft.scraprushapp.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class SaveAddress(var action: String?, var id: Int? = 0, var address: AddressData?)

@Parcelize
data class AddressData(var address: String? = "",
                       var address_type: String? = "",
                       var city: String? = "",
                       var country_id: Int? = 0,
                       var email: String? = "",
                       var mobile: Long? = 0,
                       var name: String? = "",
                       var state: String? = "",
                       var zipcode: Int? = 0,
                       var id: Int? = 0,
                       var user_id: Int? = 0,
                       var address_type_label: String? = "",
                       var formatted_address: String? = "",
                       var latitude: Double? = 0.0,
                       var longitude: Double? = 0.0,
                       var place_id: String? = "") : Parcelable

data class AddressResponse(var `data`: MutableList<AddressData> = mutableListOf(), var message: String?, var status: Boolean?)

data class CountryResponse(var `data`: List<CountryData?>?, var message: String?, var status: Boolean?)

data class CountryData(var id: Int?, var name: String?, var phonecode: Int?, var sortname: String?, var status: String?)
