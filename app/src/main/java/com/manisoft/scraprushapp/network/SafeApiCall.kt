package com.manisoft.scraprushapp.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

interface SafeApiCall {
    suspend fun <T> safeApiCall(apiCall: suspend () -> T): Resource<T> {
        return withContext(Dispatchers.IO) {
            try {
                Resource.Success(apiCall.invoke())
            } catch (throwable: Throwable) {
                when (throwable) {
                    is HttpException -> {
                        Resource.Failure(throwable, throwable.code())
                    }

                    else -> {
                        Resource.Failure(throwable, null)
                    }
                }
            }
        }
    }
}
