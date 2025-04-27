package com.manisoft.scraprushapp.models

data class NewProductRequest(var action: String? = "", var `data`: List<NewProductData?>? = listOf(), var item_id: Int? = 0)

data class NewProductData(var display_order: String? = "",
                          var image_path: String? = "",
                          var name: String? = "",
                          var name_tamil: String? = "",
                          var price: Double? = 0.0,
                          var unit_id: Int? = 0,
                          var variant_name: String? = "")