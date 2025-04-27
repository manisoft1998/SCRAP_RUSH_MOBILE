package com.manisoft.scraprushapp.utils.locationpickerutils.locationsmodel

class AddressComponents : ArrayList<AddressComponentsItem>()

data class AddressComponentsItem(
    var long_name: String?,
    var short_name: String?,
    var types: List<String?>?
)