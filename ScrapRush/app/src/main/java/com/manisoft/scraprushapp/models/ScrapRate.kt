package com.manisoft.scraprushapp.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class ScrapRateResponse(var `data`: List<ScrapRateItems> = arrayListOf(), var message: String? = "", var status: Boolean = false)

@Parcelize
data class ScrapRateItems(var id: Int = 0,
                          var name: String = "",
                          var name_tamil: String? = "",
                          var image: String? = "",
                          var price: String = "",
                          var variant_name: String = "",
                          var isSelected: Boolean = false) : Parcelable