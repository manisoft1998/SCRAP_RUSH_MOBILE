package com.manisoft.scraprushapp.network

sealed class Resource<out T> {
    data class Success<out T>(val value: T) : Resource<T>()
    data class Failure(val errorBody: Throwable?,val errorCode: Int? = null) : Resource<Nothing>()
    object Loading : Resource<Nothing>()
}