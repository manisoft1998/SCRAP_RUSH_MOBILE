package com.manisoft.scraprushapp.utils

import android.Manifest

object Constants {
    const val LOGIN_STATUS = "login_status"
    const val PROFILE_STATUS = "profile_status"
    const val FULL_NAME = "full_name"
    const val EMAIL = "email"
    const val MOBILE_NUMBER = "mobile_number"
    const val USER_IMAGE = "user_image"
    const val ACCESS_TOKEN = "access_token"
    const val GEO_LOCATION = "geo_location"
    const val IS_ADDRESS_SELECTED = "is_address_selected"
    const val SELECTED_ADDRESS = "selected_address"
    const val FCM_TOKEN = "fcm_token"
    const val USER_ROLE = "user_role"

    //drop down type
    const val ADDRESS_TYPE = "Address Type"

    //network urls
//    const val BASE_URL = "http://127.0.0.1:3000/"
//    const val BASE_URL = "http://ec2-13-236-117-43.ap-southeast-2.compute.amazonaws.com:5001/"
    const val BASE_URL = "http://ec2-13-201-10-36.ap-south-1.compute.amazonaws.com:5001/"

    const val APP_PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE

    //order status
    const val ORDER_CREATED = 1
    const val ORDER_CONFIRMED = 2
    const val WAITING_FOR_PICKUP = 3
    const val ORDER_COMPLETED = 4
    const val ORDER_REJECTED = 5
    const val ORDER_CANCELLED = 6

    //unit ids
    const val GRAMS = 1
    const val KILO_GRAMS = 2
    const val PIECE = 3
    const val LITTER = 4

    //store location
    const val STORE_LATITUDE = 11.2275588
    const val STORE_LONGITUDE = 78.1680019
}