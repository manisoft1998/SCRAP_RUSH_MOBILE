package com.manisoft.scraprushapp.mvvm.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manisoft.scraprushapp.models.LoginRequest
import com.manisoft.scraprushapp.models.LoginResponse
import com.manisoft.scraprushapp.models.UpdateProfileRequest
import com.manisoft.scraprushapp.network.Resource
import com.manisoft.scraprushapp.mvvm.repositories.LoginRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {
    //login response
    private val _loginResponse = MutableLiveData<Resource<LoginResponse>>()
    val loginResponse: LiveData<Resource<LoginResponse>> get() = _loginResponse

    fun login(loginRequest: LoginRequest) = viewModelScope.launch {
        _loginResponse.value = Resource.Loading
        _loginResponse.value = loginRepository.login(loginRequest)
    }

    //update profile response
    private val _updateProfileResponse = MutableLiveData<Resource<LoginResponse>>()
    val updateProfileResponse: LiveData<Resource<LoginResponse>> get() = _updateProfileResponse

    fun updateProfile(request: UpdateProfileRequest,accessToken:String) = viewModelScope.launch {
        _updateProfileResponse.value = Resource.Loading
        _updateProfileResponse.value = loginRepository.updateProfile(request,accessToken)
    }

    //delete account response
    private val _deleteAccountResponse = MutableLiveData<Resource<LoginResponse>>()
    val deleteAccountResponse: LiveData<Resource<LoginResponse>> get() = _deleteAccountResponse

    fun deleteAccount(accessToken:String) = viewModelScope.launch {
        _deleteAccountResponse.value = Resource.Loading
        _deleteAccountResponse.value = loginRepository.deleteAccount(accessToken)
    }

    //logout account response
    private val _logoutAccountResponse = MutableLiveData<Resource<LoginResponse>>()
    val logoutAccountResponse: LiveData<Resource<LoginResponse>> get() = _logoutAccountResponse

    fun logout(accessToken:String) = viewModelScope.launch {
        _logoutAccountResponse.value = Resource.Loading
        _logoutAccountResponse.value = loginRepository.logout(accessToken)
    }
}