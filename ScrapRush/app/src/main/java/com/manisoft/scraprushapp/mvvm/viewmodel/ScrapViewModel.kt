package com.manisoft.scraprushapp.mvvm.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manisoft.scraprushapp.models.LoginRequest
import com.manisoft.scraprushapp.models.LoginResponse
import com.manisoft.scraprushapp.models.ScrapRateResponse
import com.manisoft.scraprushapp.models.UpdateProfileRequest
import com.manisoft.scraprushapp.network.Resource
import com.manisoft.scraprushapp.mvvm.repositories.LoginRepository
import com.manisoft.scraprushapp.mvvm.repositories.ScrapRepository
import kotlinx.coroutines.launch

class ScrapViewModel(private val loginRepository: ScrapRepository) : ViewModel() {
    //login response
    private val _scrapRateResponse = MutableLiveData<Resource<ScrapRateResponse>>()
    val scrapRateResponse: LiveData<Resource<ScrapRateResponse>> get() = _scrapRateResponse

    fun getScrapRateItems(token: String) = viewModelScope.launch {
        _scrapRateResponse.value = Resource.Loading
        _scrapRateResponse.value = loginRepository.getScrapRateItems(token)
    }
}