package com.manisoft.scraprushapp.models

data class ScrapRateResponse(var `data`: List<ScrapRateItems> = arrayListOf(), var message: String?="", var status: Boolean = false)

data class ScrapRateItems(var id: Int = 0, var name: String = "", var price: String = "", var variant: String = "")