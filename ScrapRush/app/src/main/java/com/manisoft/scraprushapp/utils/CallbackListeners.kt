package com.manisoft.scraprushapp.utils

import com.manisoft.scraprushapp.utils.locationpickerutils.locationsmodel.GeocodingResult


interface MultiPermissionCallback {
    fun onAccepted()
    fun onDenied()
}
interface OnAddressPickedListener{
    fun onAddressPicked(result: GeocodingResult)
}

interface FileSelectedListener {
    fun onFileSelected(isImageType: Boolean, filePath: String, fileName: String, isFileProvider: Boolean)
}
