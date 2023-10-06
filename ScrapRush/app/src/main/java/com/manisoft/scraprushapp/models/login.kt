package com.manisoft.scraprushapp.models


//request
data class LoginRequest(var mobile_number: Long = 0)

//response
data class LoginResponse(var `data`: LoginData = LoginData(), var message: String = "", var status: Boolean = false)

data class LoginData(var is_new_user: Boolean = false, var user: LoginUser = LoginUser())

data class LoginUser(var access_token: String? = "",
                     var dob: String? = "",
                     var email_address: String? = "",
                     var id: Int = 0,
                     var image: String? = "",
                     var mobile_number: Long? = 0,
                     var name: String? = "")